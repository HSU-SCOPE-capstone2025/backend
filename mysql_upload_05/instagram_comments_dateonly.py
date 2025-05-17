import os
import re
import pandas as pd
from datetime import datetime
from sqlalchemy import create_engine, text

# ───────── DB 설정 ─────────
db_config = {
    "user": "root",
    "password": "1234",
    "host": "localhost",
    "port": "3306",
    "database": "scope",
}

engine = create_engine(
    f"mysql+mysqlconnector://{db_config['user']}:{db_config['password']}@{db_config['host']}:{db_config['port']}/{db_config['database']}"
)

# ───────── 경로 설정 ─────────
comment_folder = "/Users/syb/Desktop/mysql_upload_05/instagram_data"
stats_path = "/Users/syb/Desktop/mysql_upload_05/data/merged_instagram_stats_final.xlsx"

# ───────── 기준 데이터 로딩 ─────────
stats_df = pd.read_excel(stats_path)
stats_df["post_id"] = stats_df["post_url"].str.extract(r"\/([^/]+)/?$")
post_url_map = dict(zip(stats_df["post_id"], stats_df["post_url"]))

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
        post_id = row.get("video_url")
        post_url = post_url_map.get(post_id)

        if not post_url:
            continue  # 매핑 실패 시 생략

        # ───── 날짜 전처리: "2025년 4월 17일" 형식 처리 ─────
        raw_date = row.get("date")
        comment_date = None

        if isinstance(raw_date, str) and "년" in raw_date and "월" in raw_date and "일" in raw_date:
            try:
                dt = datetime.strptime(raw_date.strip(), "%Y년 %m월 %d일")
                comment_date = dt.strftime("%Y-%m-%d")
            except ValueError:
                comment_date = None
        else:
            # 혹시모를 datetime 객체나 다른 형식 처리
            dt = pd.to_datetime(raw_date, errors="coerce")
            comment_date = dt.strftime("%Y-%m-%d") if not pd.isna(dt) else None

        comment_data.append({
            "post_url": post_url,
            "comment": row.get("comment", ""),
            "comment_date": comment_date,
        })

# ───────── DB UPDATE: comment_date만 업데이트 ─────────

update_count = 0

with engine.begin() as conn:
    for row in comment_data:
        if not row["comment_date"]:
            continue
        if pd.isna(row["comment"]) or not row["comment"]:
            continue  # 빈 댓글은 건너뜀

        stmt = text("""
            UPDATE instagram_comment
            SET comment_date = :comment_date
            WHERE post_url = :post_url AND comment = :comment
        """)

        result = conn.execute(stmt, {
            "comment_date": row["comment_date"],
            "post_url": row["post_url"],
            "comment": row["comment"]
        })

        update_count += result.rowcount

print(f"✅ comment_date 업데이트 완료: {update_count}개 반영됨")