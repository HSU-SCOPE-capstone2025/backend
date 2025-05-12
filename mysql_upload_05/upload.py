# 한번에 올리면 Deadlock 발생 -> 순차적으로 올리기
import pandas as pd
from sqlalchemy import create_engine

# 엑셀 파일 경로 설정
file_paths = {
    "influencer": "/Users/syb/Desktop/mysql_upload_05/data/influencer_0511.xlsx",
    "youtube": "/Users/syb/Desktop/mysql_upload_05/data/final_merged_youtube_video_stats.xlsx",
    "tiktok": "/Users/syb/Desktop/mysql_upload_05/data/merged_tiktok_stats_final.xlsx",
    "instagram": "/Users/syb/Desktop/mysql_upload_05/data/merged_instagram_stats_final.xlsx",
    "ad_price": "/Users/syb/Desktop/mysql_upload_05/data/influencer_ad_price_estimation.xlsx",
    "total_followers": "/Users/syb/Desktop/mysql_upload_05/data/total_follower_all_platforms_final.xlsx",
    "youtube_lang": "/Users/syb/Desktop/mysql_upload_05/data/youtube_data_language_summary.xlsx",
    "tiktok_lang": "/Users/syb/Desktop/mysql_upload_05/data/tiktok_data_language_summary.xlsx",
    "insta_lang": "/Users/syb/Desktop/mysql_upload_05/data/instagram_data_language_summary.xlsx"
}

# MySQL 연결 정보
DB_CONFIG = {
    "user": "root",
    "password": "1234",
    "host": "localhost",
    "port": 3306,
    "database": "scope"
}

# SQLAlchemy 연결 문자열
conn_str = f"mysql+mysqlconnector://{DB_CONFIG['user']}:{DB_CONFIG['password']}@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}?charset=utf8mb4"
engine = create_engine(conn_str)

# 업로드 함수 (속도 개선: chunksize, 오류 핸들링)
def upload_excel_to_mysql(file_path, table_name):
    try:
        print(f"📄 {table_name} 업로드 중...")
        df = pd.read_excel(file_path)
        df.columns = [col.strip() for col in df.columns]
        df.to_sql(name=table_name, con=engine, if_exists="replace", index=False, chunksize=1000)
        print(f"✅ {table_name} 테이블 업로드 완료\n")
    except Exception as e:
        print(f"❌ {table_name} 업로드 실패: {e}\n")

# 전체 테이블 업로드 실행
for key, path in file_paths.items():
    upload_excel_to_mysql(path, key if "_lang" not in key else key.replace("_lang", "_language_summary"))