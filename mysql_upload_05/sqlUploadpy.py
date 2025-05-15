import pandas as pd
import os
import mysql.connector

# MySQL 연결 정보
HOST = "localhost"  # EC2 인스턴스 또는 RDS 주소
USER = "root"       # MySQL 사용자 이름
PASSWORD = "0254"  # MySQL 비밀번호
DATABASE = "scope"  # 사용할 데이터베이스 이름

# 엑셀 파일 경로
EXCEL_DIR = r"E:\My_Project\Scope_backend\mysql_upload_05\data"

def connect_to_mysql():
    """ MySQL 데이터베이스에 연결 """
    try:
        connection = mysql.connector.connect(
            host=HOST,
            user=USER,
            password=PASSWORD,
            database=DATABASE
        )
        if connection.is_connected():
            print("MySQL에 연결되었습니다.")
        return connection
    except mysql.connector.Error as err:
        print(f"Error: {err}")
        return None

def insert_data_to_mysql(table_name, df, connection):
    """ 데이터프레임의 데이터를 MySQL에 삽입 """
    cursor = connection.cursor()
    # 데이터 삽입
    for index, row in df.iterrows():
        columns = ', '.join(row.index)
        placeholders = ', '.join(['%s'] * len(row))
        sql = f"INSERT INTO {table_name} ({columns}) VALUES ({placeholders})"
        try:
            cursor.execute(sql, tuple(row))
            connection.commit()
        except mysql.connector.Error as err:
            print(f"데이터 삽입 중 오류 발생: {err}")
            connection.rollback()
    cursor.close()

def process_excel_file(file_path, connection):
    """ 엑셀 파일을 읽어 데이터 삽입 """
    try:
        df = pd.read_excel(file_path)
        table_name = os.path.splitext(os.path.basename(file_path))[0]
        print(f"테이블: {table_name}, 행 수: {len(df)}")
        insert_data_to_mysql(table_name, df, connection)
    except Exception as e:
        print(f"파일 처리 중 오류 발생: {e}")

def main():
    connection = connect_to_mysql()
    if connection is None:
        print("MySQL 연결에 실패했습니다.")
        return

    for file_name in os.listdir(EXCEL_DIR):
        if file_name.endswith(".xlsx"):
            file_path = os.path.join(EXCEL_DIR, file_name)
            print(f"처리 중인 파일: {file_name}")
            process_excel_file(file_path, connection)

    connection.close()
    print("모든 파일 처리가 완료되었습니다.")

if __name__ == "__main__":
    main()
