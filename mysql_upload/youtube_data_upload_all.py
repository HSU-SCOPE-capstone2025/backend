# n월 n일~ n월 n일까지의 데이터 한꺼번에 올리기 

import os
import pandas as pd
import mysql.connector
from datetime import datetime, timedelta

def safe_get(val):
    return None if pd.isna(val) else val

def get_connection():
    return mysql.connector.connect(
        host='localhost',
        user='',
        password='',
        database='',
        charset='utf8mb4'
    )

def get_influencer_id(name, cursor):
    cursor.execute("SELECT id FROM influencer WHERE name = %s", (name,))
    result = cursor.fetchone()
    if result:
        return result[0]
    cursor.execute("INSERT INTO influencer (name) VALUES (%s)", (name,))
    return cursor.lastrowid

def upload_youtube_data(stats_path, comments_path, mapping_path):
    if not os.path.exists(stats_path):
        print(f"📁 통계 파일 없음 → 건너뜀: {stats_path}")
        return
    if not os.path.exists(comments_path):
        print(f"📁 댓글 파일 없음 → 건너뜀: {comments_path}")
        return

    stats_df = pd.read_excel(stats_path)
    comments_df = pd.read_excel(comments_path)
    mapping_df = pd.read_excel(mapping_path)

    conn = get_connection()
    cursor = conn.cursor()

    for _, row in stats_df.iterrows():
        channel_title = safe_get(row['channel_title'])
        mapping_row = mapping_df[mapping_df['youtube'] == channel_title]
        if mapping_row.empty:
            print(f"❌ 매핑 실패: {channel_title}")
            continue
        name = mapping_row.iloc[0]['name']
        influencer_id = get_influencer_id(name, cursor)

        cursor.execute("""
            INSERT INTO youtube_follower (
                influencer_id, name, channel_id, channel_title, uploaded_at,
                subscriber_count, channel_description
            )
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
                subscriber_count = VALUES(subscriber_count),
                channel_description = VALUES(channel_description),
                uploaded_at = VALUES(uploaded_at),
                channel_title = VALUES(channel_title)
        """, (
            influencer_id,
            name,
            safe_get(row['channel_id']),
            channel_title,
            safe_get(row['date']),
            safe_get(row['subscriber_count']),
            safe_get(row['channel_description'])
        ))

        print(f"📹 영상 처리 중: {safe_get(row['title'])} ({safe_get(row['video_id'])})")

        cursor.execute("SELECT id FROM youtube_video WHERE video_uid = %s", (safe_get(row['video_id']),))
        existing = cursor.fetchone()
        if existing:
            print("🔁 이미 존재함 → update 예정")
        else:
            print("🆕 신규 영상 → insert 예정")

        cursor.execute("""
            INSERT INTO youtube_video (
                channel_id, name, uploaded_at, published_at, view_count, like_count,
                comment_count, topic_categories, title, description, tags, thumbnails, video_uid
            )
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
                view_count = VALUES(view_count),
                like_count = VALUES(like_count),
                comment_count = VALUES(comment_count)
        """, (
            safe_get(row['channel_id']),
            name,
            safe_get(row['date']),
            safe_get(row['upload_date']),
            safe_get(row['view_count']),
            safe_get(row['like_count']),
            safe_get(row['comment_count']),
            safe_get(row['topic_categories']),
            safe_get(row['title']),
            safe_get(row['description']),
            safe_get(row['tags']),
            safe_get(row['thumbnails']),
            safe_get(row['video_id'])
        ))

    for _, row in comments_df.iterrows():
        cursor.execute("SELECT id FROM youtube_video WHERE video_uid = %s", (safe_get(row['video_id']),))
        result = cursor.fetchone()
        if not result:
            continue
        video_id = result[0]

        cursor.execute("""
            SELECT COUNT(*) FROM youtube_video_detail
            WHERE video_id = %s AND username = %s AND comment = %s AND commented_at = %s
        """, (
            video_id,
            safe_get(row['author']),
            safe_get(row['comment']),
            safe_get(row['date'])
        ))
        exists = cursor.fetchone()[0]

        if exists == 0:
            cursor.execute("""
                INSERT INTO youtube_video_detail (
                    video_id, username, comment, commented_at, like_count, created_at
                )
                VALUES (%s, %s, %s, %s, %s, %s)
            """, (
                video_id,
                safe_get(row['author']),
                safe_get(row['comment']),
                safe_get(row['date']),
                safe_get(row['num_likes']),
                datetime.now()
            ))

    conn.commit()
    cursor.close()
    conn.close()
    print("✅ 업로드 완료:", os.path.basename(stats_path))

# 실행 (범위 반복)
if __name__ == "__main__":
    mapping_file = '/Users/syb/Desktop/capstone_influencer/influencer_namimg.xlsx'
    stats_dir = '/Users/syb/Desktop/youtube_crawling/data_stats'
    comments_dir = '/Users/syb/Desktop/youtube_crawling/data_crawling_auto'

    start_date = datetime(2025, 2, 23)
    end_date = datetime(2025, 4, 13)

    current = start_date
    while current <= end_date:
        file_suffix = current.strftime('%y%m%d')
        stats_file = f'{stats_dir}/stats_{file_suffix}.xlsx'
        comments_file = f'{comments_dir}/crawling_auto_{file_suffix}.xlsx'
        print(f"📅 처리 날짜: {current.date()}")

        upload_youtube_data(stats_file, comments_file, mapping_file)
        current += timedelta(days=1)
