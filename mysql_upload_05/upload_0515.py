import pandas as pd
from sqlalchemy import create_engine, text

# DB 설정
db_config = {
    "user": "root",
    "password": "1234",
    "host": "localhost",
    "port": "3306",
    "database": "test0514",
}

# 파일 경로
file_paths = {
    "influencer": "/Users/syb/Desktop/mysql_upload_05/data/influencer_0511.xlsx",
    "youtube": "/Users/syb/Desktop/mysql_upload_05/data/final_merged_youtube_video_stats.xlsx",
    "tiktok": "/Users/syb/Desktop/mysql_upload_05/data/merged_tiktok_stats_final.xlsx",
    "instagram": "/Users/syb/Desktop/mysql_upload_05/data/merged_instagram_stats_final.xlsx",
    "ad_price": "/Users/syb/Desktop/mysql_upload_05/data/influencer_ad_price_estimation.xlsx",
    "total_follower": "/Users/syb/Desktop/mysql_upload_05/data/total_follower_all_platforms_final.xlsx",
    "youtube_language": "/Users/syb/Desktop/mysql_upload_05/data/youtube_data_language_summary.xlsx",
    "tiktok_language": "/Users/syb/Desktop/mysql_upload_05/data/tiktok_data_language_summary.xlsx",
    "instagram_language": "/Users/syb/Desktop/mysql_upload_05/data/instagram_data_language_summary.xlsx",
}

# 데이터 로딩
dfs = {name: pd.read_excel(path) for name, path in file_paths.items()}
dfs["youtube"]["video_url"] = dfs["youtube"]["video_url"].astype(str)
dfs["tiktok"]["video_url"] = dfs["tiktok"]["video_url"].astype(str)
dfs["instagram"]["post_url"] = dfs["instagram"]["post_url"].astype(str)

# DB 연결
engine = create_engine(
    f"mysql+mysqlconnector://{db_config['user']}:{db_config['password']}@{db_config['host']}:{db_config['port']}/{db_config['database']}"
)

# 외래 키 잠깐 끄기
with engine.connect() as conn:
    conn.execute(text("SET FOREIGN_KEY_CHECKS=0;"))

# influencer 테이블을 직접 생성 (PK 포함!)
with engine.connect() as conn:
    conn.execute(text("""
        DROP TABLE IF EXISTS influencer;
    """))
    conn.execute(text("""
        CREATE TABLE influencer (
            influencer_num BIGINT PRIMARY KEY,
            influencer_name TEXT,
            categories TEXT,
            tags TEXT
        );
    """))

# influencer 데이터만 따로 insert
dfs["influencer"].to_sql("influencer", con=engine, if_exists="append", index=False)

# 나머지 테이블 업로드 (replace 가능)
upload_order = [
    "ad_price",
    "youtube",
    "tiktok",
    "instagram",
    "total_follower",
    "youtube_language",
    "tiktok_language",
    "instagram_language",
]

for table in upload_order:
    print(f"📥 Uploading: {table}")
    dfs[table].to_sql(name=table, con=engine, if_exists="replace", index=False)

# 외래 키 다시 활성화
with engine.connect() as conn:
    conn.execute(text("SET FOREIGN_KEY_CHECKS=1;"))

print("✅ 모든 테이블 업로드 완료!")