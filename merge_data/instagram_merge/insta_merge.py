import os
import glob
import pandas as pd
from datetime import datetime
import dateutil

# === 경로 설정 ===
BASE_DIR = "instagram_data"
POST_DIR = os.path.join(BASE_DIR, "influencer")
FOLLOWER_DIR = os.path.join(BASE_DIR, "influencers_list")
OUTPUT_DIR = os.path.join(BASE_DIR, "stats_result")

# === 가장 최신 influencers_list 파일 불러오기 ===
list_files = sorted(glob.glob(os.path.join(FOLLOWER_DIR, "influencers_list_*.xlsx")), reverse=True)
if not list_files:
    raise FileNotFoundError("❌ influencers_list 폴더에 파일이 없습니다.")

latest_followers = pd.read_excel(list_files[0])
latest_followers.rename(columns={
    "인플루언서": "influencer",
    "팔로워 수": "follower_number",
    "데이터 수집일": "at_time"
}, inplace=True)
latest_followers = latest_followers[["influencer", "follower_number", "at_time"]]

# === 숫자 문자열 처리 함수 ===
def parse_count(value):
    if isinstance(value, str):
        value = value.replace(",", "").strip()
        if "만" in value:
            return int(float(value.replace("만", "")) * 10000)
        elif "천" in value:
            return int(float(value.replace("천", "")) * 1000)
        elif value.isdigit():
            return int(value)
    try:
        return int(float(value))
    except:
        return None

# === 날짜 파싱 함수 ===
def parse_date_str(date_str):
    try:
        # 문자열 정리: '.', '/', '년월일' 등을 '-'로 통일
        if isinstance(date_str, str):
            clean_str = (
                date_str.replace("년", "-")
                .replace("월", "-")
                .replace("일", "")
                .replace(".", "-")
                .replace("/", "-")
                .strip()
            )
        else:
            clean_str = str(date_str)

        # dateutil로 파싱
        date_obj = dateutil.parser.parse(clean_str, fuzzy=True)
        return date_obj.strftime('%Y-%m-%d')
    except Exception:
        return None

# === 댓글 저장 엑셀에서 게시물 통계 추출 ===
all_stats = []

for folder in sorted(os.listdir(POST_DIR)):
    folder_path = os.path.join(POST_DIR, folder)
    if not os.path.isdir(folder_path):
        continue

    excel_files = glob.glob(os.path.join(folder_path, "*.xlsx"))
    for file_path in excel_files:
        try:
            df = pd.read_excel(file_path)
            df.ffill(inplace=True)

            required_cols = ["게시물 URL", "게시물 날짜", "게시물 좋아요 수", "댓글 수", "데이터 수집일"]
            if not all(col in df.columns for col in required_cols):
                missing = [col for col in required_cols if col not in df.columns]
                print(f"⚠️ 누락된 컬럼 {missing} → 건너뜀: {file_path}")
                continue

            # 인플루언서 이름 추출
            filename = os.path.basename(file_path)
            influencer = filename.split(folder)[0]

            trimmed = df[required_cols].drop_duplicates(subset=["게시물 URL"], keep="last")
            trimmed.insert(0, "influencer", influencer)
            trimmed.rename(columns={
                "게시물 URL": "post_url",
                "게시물 날짜": "post_date",
                "게시물 좋아요 수": "like_count",
                "댓글 수": "comment_count",
                "데이터 수집일": "at_time"
            }, inplace=True)

            trimmed["like_count"] = trimmed["like_count"].apply(parse_count)
            trimmed["comment_count"] = trimmed["comment_count"].apply(parse_count)
            trimmed["post_date"] = trimmed["post_date"].apply(parse_date_str)

            all_stats.append(trimmed)

        except Exception as e:
            print(f"❌ 오류 발생 ({file_path}): {e}")

# === 전체 병합 ===
if not all_stats:
    raise ValueError("❌ 수집된 데이터가 없습니다.")

merged_df = pd.concat(all_stats, ignore_index=True)

# === 팔로워 수 병합
final_df = pd.merge(merged_df, latest_followers, on=["influencer", "at_time"], how="left")

# === 결측 follower_number 보정
if final_df["follower_number"].isna().any():
    latest_by_name = latest_followers.sort_values("at_time").drop_duplicates("influencer", keep="last")
    final_df = pd.merge(
        final_df,
        latest_by_name[["influencer", "follower_number"]],
        on="influencer",
        how="left",
        suffixes=('', '_latest')
    )
    final_df["follower_number"] = final_df["follower_number"].fillna(final_df["follower_number_latest"])
    final_df.drop(columns=["follower_number_latest"], inplace=True)

# === 중복 제거 및 정렬
final_df.sort_values(by="at_time", ascending=False, inplace=True)
final_df.drop_duplicates(subset=["post_url"], keep="first", inplace=True)
final_df.sort_values(by=["influencer", "post_date"], inplace=True)

# === 저장
os.makedirs(OUTPUT_DIR, exist_ok=True)
save_path = os.path.join(OUTPUT_DIR, f"insta_stats_{datetime.today().strftime('%Y%m%d')}.xlsx")
final_df.to_excel(save_path, index=False)

print(f"✅ 통합 저장 완료: {save_path}")