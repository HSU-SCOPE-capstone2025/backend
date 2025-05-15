import pandas as pd
import mysql.connector
from mysql.connector import Error
import os

# MySQL 연결 정보
db_config = {
    'user': 'root',
    'password': '0254',
    'host': '127.0.0.1',
    'database': 'scope'
}

# 파일 경로 설정
file_paths = {
    'youtube_language_summary': 'E:\My_Project\Scope_backend\mysql_upload_05\data\youtube_language_summary.xlsx',
    'instagram_language_summary': 'E:\My_Project\Scope_backend\mysql_upload_05\data\instagram_language_summary.xlsx',
    'tiktok_language_summary': 'E:\My_Project\Scope_backend\mysql_upload_05\data\tiktok_language_summary.xlsx'
}

# 테이블 업로드 함수
def upload_to_mysql(table_name, df):
    try:
        connection = mysql.connector.connect(**db_config)
        if connection.is_connected():
            cursor = connection.cursor()
            # 데이터프레임을 MySQL 테이블에 업로드
            for index, row in df.iterrows():
                columns = ', '.join(row.index)
                values = ', '.join(['%s'] * len(row))
                sql = f"INSERT INTO {table_name} ({columns}) VALUES ({values})"
                cursor.execute(sql, tuple(row))
            connection.commit()
            print(f"{table_name} 업로드 완료")
    except Error as e:
        print(f"Error: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

# 엑셀 파일 읽기 및 업로드
for table_name, path in file_paths.items():
    if os.path.exists(path):
        df = pd.read_excel(path)
        print(f"{table_name} 업로드 중...")
        upload_to_mysql(table_name, df)
    else:
        print(f"{path} 파일을 찾을 수 없습니다.")
""
