import os
import re
import pandas as pd
from datetime import datetime
from sqlalchemy import create_engine, text
from sqlalchemy.pool import NullPool
import time

# ───────── DB 설정 ─────────
db_config = {
    "user": "root",
    "password": "0254",
    "host": "localhost",
    "port": "3306",
    "database": "aaa",
}

print("🚀 MySQL 연결 시도 중...")
print(f"🔍 연결 문자열: mysql+mysqlconnector://{db_config['user']}:{db_config['password']}@{db_config['host']}:{db_config['port']}/{db_config['database']}")

try:
    engine = create_engine(
        f"mysql+mysqlconnector://{db_config['user']}:{db_config['password']}@{db_config['host']}:{db_config['port']}/{db_config['database']}",
        pool_pre_ping=True,          # MySQL 커넥션 유효성 체크
        pool_recycle=3600,           # 1시간마다 커넥션 갱신
        poolclass=NullPool           # 풀링 비활성화 (직접 커넥션 확인)
    )
    print("✅ MySQL 엔진 생성 성공")
    
    print("🚀 MySQL 커넥션 시도 중... (10초 제한)")
    start_time = time.time()
    
    with engine.connect() as conn:
        elapsed = time.time() - start_time
        print(f"✅ MySQL 연결 성공 (연결 시간: {elapsed}초)")
        
        # 🔎 현재 연결된 데이터베이스가 맞는지 확인
        try:
            result = conn.execute(text("SELECT DATABASE()"))
            db_name = result.fetchone()[0]
            print(f"🔍 현재 연결된 데이터베이스: {db_name}")
        except Exception as query_error:
            print(f"❌ DB 쿼리 실행 에러: {query_error}")
            exit()
except Exception as e:
    print(f"❌ MySQL 연결 실패: {e}")
    exit()

# ───────── DB Pool 상태 확인 ─────────
try:
    print("🚀 Pool 상태 확인")
    print(f"🗂 Pool size: {engine.pool.size()} | Checked out: {engine.pool.checkedout()}")
except Exception as pool_error:
    print(f"❌ Pool 상태 확인 에러: {pool_error}")
    exit()

# ───────── 경로 설정 ─────────
comment_folder = "E:\My_Project\Scope_backend\mysql_upload_05\instagram_data"
stats_path = "E:\My_Project\Scope_backend\mysql_upload_05\data\merged_instagram_stats_final.xlsx"

# ───────── 기준 데이터 로딩 ─────────
print("🚀 Excel 파일 로딩 중...")
try:
    stats_df = pd.read_excel(stats_path)
    stats_df["post_id"] = stats_df["post_url"].str.extract(r"\/([^/]+)/?$")
    post_url_map = dict(zip(stats_df["post_id"], stats_df["post_url"]))
    print(f"✅ 데이터 로딩 완료, 로우 수: {len(stats_df)}")
except Exception as e:
    print(f"❌ Excel 파일 로딩 실패: {e}")
    exit()

# ───────── 댓글 파일 반복 처리 ─────────
comment_data = []
print("🚀 댓글 파일 탐색 중...")

for filename in os.listdir(comment_folder):
    if not filename.endswith(".xlsx") or filename.startswith("~$"):
        continue

    filepath = os.path.join(comment_folder, filename)

    try:
        print(f"✅ 로딩 중: {filename}")
        df = pd.read_excel(filepath, engine="openpyxl")
        print(f"📄 {filename} 로딩 성공, 로우 수: {len(df)}")
    except Exception as e:
        print(f"❌ [무시됨] 문제 파일: {filename} → {e}")
        continue

    for _, row in df.iterrows():
        post_id = row["video_url"]
        post_url = post_url_map.get(post_id)

        if not post_url:
            continue  # 매핑 실패 시 생략

        # 날짜 안전 처리
        dt = pd.to_datetime(row.get("date"), errors="coerce")
        comment_date = dt.strftime("%Y-%m-%d") if not pd.isna(dt) else None

        score = row.get("score")
        fss = row.get("FSS")

        comment_data.append({
            "post_url": post_url,
            "comment": row.get("comment", ""),
            "comment_date": comment_date,
            "emotion": row.get("감정"),
            "topic": row.get("주제"),
            "cluster": row.get("군집"),
            "score": int(score) if pd.notna(score) else 0,
            "fss": float(fss) if pd.notna(fss) else 0.0
        })

# ───────── DataFrame으로 변환 후 업로드 ─────────
comment_df = pd.DataFrame(comment_data)
print(f"🚀 DataFrame 생성 완료, 총 {len(comment_df)}개 로우")
print("🔍 DataFrame 컬럼 목록:", list(comment_df.columns))

try:
    with engine.connect() as conn:
        print("🚀 DB 업로드 시작")
        
        # DB 컬럼 확인
        print("🔍 DB 테이블 컬럼 확인 중...")
        cursor = conn.execute(text("DESCRIBE instagram_comment"))
        db_columns = [row[0] for row in cursor.fetchall()]
        print(f"📌 DB Columns: {db_columns}")
        
        # 컬럼이 일치하는지 체크
        if set(db_columns) == set(comment_df.columns):
            print("✅ DB 컬럼과 DataFrame 컬럼이 일치합니다.")
            conn.execute(text("SET FOREIGN_KEY_CHECKS=0;"))
            comment_df.to_sql("instagram_comment", con=engine, if_exists="append", index=False)
            conn.execute(text("SET FOREIGN_KEY_CHECKS=1;"))
            print(f"✅ instagram_comment 테이블에 {len(comment_df)}개 댓글 업로드 완료!")
        else:
            print(f"❌ DB 컬럼과 DataFrame 컬럼이 일치하지 않습니다.")
            print(f"🔍 DB: {db_columns}")
            print(f"🔍 DataFrame: {list(comment_df.columns)}")
except Exception as e:
    print(f"❌ DB 업로드 중 에러 발생: {e}")
