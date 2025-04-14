# 정해진 일자 데이터 업로드 mysql

import pandas as pd
import mysql.connector
from datetime import datetime

# NaN-safe getter
def safe_get(val):
    return None if pd.isna(val) else val

# MySQL 연결
def get_connection():
    return mysql.connector.connect(
        host='localhost',
        user='', # 개인 mysql username
        password='', # 개인 mysql 비밀번호
        database='', # 데이터베이스 입력 ex)sns_data
        charset='utf8mb4'
    )

# influencer.id 조회 또는 생성
def get_influencer_id(name, cursor):
    cursor.execute("SELECT id FROM influencer WHERE name = %s", (name,))
    result = cursor.fetchone()
    if result:
        return result[0]
    cursor.execute("INSERT INTO influencer (name) VALUES (%s)", (name,))
    return cursor.lastrowid

# 메인 업로드 함수
def upload_youtube_data(stats_path, comments_path, mapping_path):
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

        # 유튜브 채널 정보 저장
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

        # ▶️ 영상 로그
        print(f"📹 영상 처리 중: {safe_get(row['title'])} ({safe_get(row['video_id'])})")

        # 영상이 이미 존재하는지 확인
        cursor.execute("SELECT id FROM youtube_video WHERE video_uid = %s", (safe_get(row['video_id']),))
        existing = cursor.fetchone()
        if existing:
            print("🔁 이미 존재함 → update 예정")
        else:
            print("🆕 신규 영상 → insert 예정")

        # 영상 정보 저장
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

    # 댓글 정보 저장
    for _, row in comments_df.iterrows():
        cursor.execute("SELECT id FROM youtube_video WHERE video_uid = %s", (safe_get(row['video_id']),))
        result = cursor.fetchone()
        if not result:
            continue
        video_id = result[0]

        # 중복 댓글 체크
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
    print("✅ 유튜브 데이터 업로드 완료!")

# 실행
if __name__ == "__main__":
    target_date = "2025-04-05"
    file_suffix = target_date[2:4] + target_date[5:7] + target_date[8:10]

    stats_file = f'/Users/syb/Desktop/youtube_crawling/data_stats/stats_{file_suffix}.xlsx'
    comments_file = f'/Users/syb/Desktop/youtube_crawling/data_crawling_auto/crawling_auto_{file_suffix}.xlsx'
    mapping_file = '/Users/syb/Desktop/capstone_influencer/influencer_namimg.xlsx'

    upload_youtube_data(stats_file, comments_file, mapping_file)
