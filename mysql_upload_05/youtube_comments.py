import os
import re
import pandas as pd
from sqlalchemy import create_engine, text

# ───────── DB 설정 ─────────
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

# ───────── 경로 설정 ─────────
comment_folder = "/Users/syb/Desktop/mysql_upload_05/youtube_data"
stats_path = "/Users/syb/Desktop/mysql_upload_05/data/final_merged_youtube_video_stats.xlsx"

# ───────── 기준 video_url 매핑 만들기 ─────────
stats_df = pd.read_excel(stats_path)
stats_df["video_id"] = stats_df["video_url"].str.extract(r"(?:v=|\/)([a-zA-Z0-9_-]{11})")
video_url_map = dict(zip(stats_df["video_id"], stats_df["video_url"]))

# ───────── 댓글 파일 반복 처리 ─────────
comment_data = []

for filename in os.listdir(comment_folder):
    if not filename.endswith(".xlsx") or filename.startswith("~$"):
        continue

    filepath = os.path.join(comment_folder, filename)
    try:
        df = pd.read_excel(filepath, engine="openpyxl")
    except Exception as e:
        print(f"❌ [무시됨] 문제 파일: {filename} → {e}")
        continue

    for _, row in df.iterrows():
        # video_url에서 video_id 추출
        video_id_match = re.search(r"([a-zA-Z0-9_-]{11})", str(row.get("video_url")))
        video_id = video_id_match.group(1) if video_id_match else None

        video_url = video_url_map.get(video_id)
        if not video_url:
            continue  # 매핑 안 되면 생략

        # 날짜 안전 처리
        dt = pd.to_datetime(row.get("date"), errors="coerce")
        comment_date = dt.strftime("%Y-%m-%d") if not pd.isna(dt) else None

        score = row.get("score")
        fss = row.get("FSS")

        comment_data.append({
            "video_url": video_url,
            "comment": row.get("comment", ""),
            "comment_date": comment_date,
            "emotion": row.get("감정"),
            "topic": row.get("주제"),
            "cluster": row.get("군집"),
            "score": int(score) if pd.notna(score) else 0,
            "fss": float(fss) if pd.notna(fss) else 0.0
        })

# ───────── 업로드 ─────────
comment_df = pd.DataFrame(comment_data)

with engine.connect() as conn:
    conn.execute(text("SET FOREIGN_KEY_CHECKS=0;"))

comment_df.to_sql("youtube_comment", con=engine, if_exists="append", index=False)

with engine.connect() as conn:
    conn.execute(text("SET FOREIGN_KEY_CHECKS=1;"))

print(f"✅ youtube_comment 테이블에 댓글 {len(comment_df)}개 업로드 완료!")