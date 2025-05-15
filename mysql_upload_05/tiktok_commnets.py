import os
import pandas as pd
from sqlalchemy import create_engine, text

# ────── DB 설정 ──────
db_config = {
    "user": "root",
    "password": "1234",
    "host": "localhost",
    "port": "3306",
    "database": "test0514",
}

engine = create_engine(
    f"mysql+mysqlconnector://{db_config['user']}:{db_config['password']}@{db_config['host']}:{db_config['port']}/{db_config['database']}"
)

# ────── 경로 설정 ──────
comment_folder = "/Users/syb/Desktop/mysql_upload_05/tiktok_data"
stats_path = "/Users/syb/Desktop/mysql_upload_05/data/merged_tiktok_stats_final.xlsx"

# ────── 기준 video_url 매핑 ──────
stats_df = pd.read_excel(stats_path)
stats_df["video_url"] = stats_df["video_url"].astype(str).str.strip()
video_url_set = set(stats_df["video_url"])

# ────── 댓글 파일 반복 처리 및 업로드 ──────
with engine.connect() as conn:
    conn.execute(text("SET FOREIGN_KEY_CHECKS=0;"))

for filename in os.listdir(comment_folder):
    if not filename.endswith(".xlsx") or filename.startswith("~$"):
        continue

    filepath = os.path.join(comment_folder, filename)
    try:
        df = pd.read_excel(filepath, engine="openpyxl")
    except Exception as e:
        print(f"❌ [무시됨] 문제 파일: {filename} → {e}")
        continue

    comment_data = []
    for _, row in df.iterrows():
        video_url = str(row.get("video_url")).strip()
        if video_url not in video_url_set:
            continue

        dt = pd.to_datetime(row.get("date"), errors="coerce")
        comment_date = dt.strftime("%Y-%m-%d") if not pd.isna(dt) else None

        score = row.get("score")
        fss = row.get("FSS")

        comment_data.append({
            "video_url": video_url,
            "comment": str(row.get("comment", "")),
            "comment_date": comment_date,
            "emotion": row.get("감정"),
            "topic": row.get("주제"),
            "cluster": row.get("군집"),
            "score": int(score) if pd.notna(score) else 0,
            "fss": float(fss) if pd.notna(fss) else 0.0
        })

    if comment_data:
        comment_df = pd.DataFrame(comment_data)
        try:
            # 200개씩 나눠서 업로드
            for start in range(0, len(comment_df), 200):
                chunk = comment_df.iloc[start:start+200]
                chunk.to_sql("tiktok_comment", con=engine, if_exists="append", index=False)
            print(f"✅ {filename} → {len(comment_df)}개 업로드 완료")
        except Exception as e:
            print(f"❌ [실패] {filename} 업로드 실패: {e}")

with engine.connect() as conn:
    conn.execute(text("SET FOREIGN_KEY_CHECKS=1;"))