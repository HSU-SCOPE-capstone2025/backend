import os
import pandas as pd
import mysql.connector
from mysql.connector import Error

# MySQL database connection
def create_connection():
    try:
        connection = mysql.connector.connect(
            host='localhost',
            user='root',
            password='0254',
            database='scope'
        )
        if connection.is_connected():
            print("Successfully connected to MySQL")
        return connection
    except Error as e:
        print(f"Error: {e}")
        return None

# Load Excel files
def load_excel_files(directory):
    dataframes = {}
    for file in os.listdir(directory):
        if file.endswith('.xlsx'):
            file_path = os.path.join(directory, file)
            df = pd.read_excel(file_path)
            dataframes[file.replace('.xlsx', '')] = df
            print(f"Loaded {file}")
    return dataframes

# Insert data into MySQL
def insert_data(df, table_name, connection):
    cursor = connection.cursor()
    columns = ', '.join(df.columns)
    values = ', '.join(['%s'] * len(df.columns))
    query = f"INSERT INTO {table_name} ({columns}) VALUES ({values})"

    for _, row in df.iterrows():
        try:
            cursor.execute(query, tuple(row))
        except Error as e:
            print(f"Failed to insert row into {table_name}: {e}")

    connection.commit()
    print(f"Data inserted into {table_name}")

# Main logic
def main():
    directory = '/Scope_backend/mysql_upload_05/data'
    connection = create_connection()
    if connection:
        dataframes = load_excel_files(directory)
        
        # Mapping tables to DataFrames
        table_mapping = {
            'instagram_data_language_summary': 'instagram_language_summary',
            'merged_instagram_stats_final': 'instagram_post',
            'merged_tiktok_stats_final': 'tiktok_video',
            'tiktok_data_language_summary': 'tiktok_language_summary',
            'total_follower_all_platforms_final': 'total_followers',
            'youtube_data_language_summary': 'youtube_language_summary',
            'final_merged_youtube_video_stats': 'youtube_video',
            'influencer_0511': 'influencer',
            'influencer_ad_price_estimation': 'ad_price_estimation'
        }
        
        for excel_name, table_name in table_mapping.items():
            if excel_name in dataframes:
                print(f"Inserting data into {table_name}")
                insert_data(dataframes[excel_name], table_name, connection)
        
        connection.close()
        print("MySQL connection closed.")

if __name__ == "__main__":
    main()
    
