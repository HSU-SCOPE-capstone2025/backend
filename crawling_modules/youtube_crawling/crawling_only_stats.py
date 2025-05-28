import pandas as pd
from googleapiclient.discovery import build
from datetime import datetime
import re
import warnings

# 경고 무시
warnings.filterwarnings('ignore')

# Excel 불법 문자 제거
def clean_illegal_characters(text):
    if isinstance(text, str):
        return re.sub(r'[\x00-\x08\x0B-\x0C\x0E-\x1F]', '', text)
    return text

def clean_dataframe_strings(df):
    for col in df.select_dtypes(include=[object]).columns:
        df[col] = df[col].apply(clean_illegal_characters)
    return df

# API 초기화
def initialize_api(api_key):
    return build('youtube', 'v3', developerKey=api_key)

# 영상 정보
def get_video_info(api_obj, video_id):
    response = api_obj.videos().list(
        part='snippet,statistics,topicDetails',
        id=video_id
    ).execute()
    video_info = response['items'][0]
    return video_info['snippet'], video_info['statistics'], video_info.get('topicDetails', {})

# 통계 저장
def save_statistics_to_excel(stats_df, file_path):
    stats_df = clean_dataframe_strings(stats_df)
    try:
        existing_stats_df = pd.read_excel(file_path)
        stats_df = pd.concat([existing_stats_df, stats_df], ignore_index=True)
    except FileNotFoundError:
        pass
    stats_df.to_excel(file_path, index=False)
    print(f"✅ 통계 정보를 {file_path}에 저장했습니다.")

# 채널 정보
def get_channel_info(api_obj, channel_id):
    response = api_obj.channels().list(
        part='snippet,statistics',
        id=channel_id
    ).execute()
    return response['items'][0]['snippet'], response['items'][0]['statistics']

# 최신 영상 ID
def get_latest_videos(api_obj, channel_id, max_results=7):
    response = api_obj.search().list(
        part='snippet',
        channelId=channel_id,
        order='date',
        maxResults=max_results,
        type='video'
    ).execute()

    return [item['id']['videoId'] for item in response.get('items', []) if 'videoId' in item['id']]

# 메인 함수: 댓글 수집 없이 통계만 저장
def extract_latest_videos_from_channel(channel_id, STATS_FILE_PATH):
    api_obj = initialize_api(API_KEY)
    video_ids = get_latest_videos(api_obj, channel_id)

    for video_id in video_ids:
        snippet, statistics, topic_details = get_video_info(api_obj, video_id)
        current_date = datetime.now().strftime('%Y-%m-%d')
        upload_date = datetime.strptime(snippet['publishedAt'], '%Y-%m-%dT%H:%M:%SZ').strftime('%Y-%m-%d')
        channel_snippet, channel_statistics = get_channel_info(api_obj, channel_id)

        stats_data = {
            'video_id': video_id,
            'upload_date': [upload_date],
            'date': [current_date],
            'view_count': [statistics.get('viewCount')],
            'like_count': [statistics.get('likeCount')],
            'comment_count': [statistics.get('commentCount')],
            'subscriber_count': [channel_statistics.get('subscriberCount', 0)],
            'channel_id': [channel_id],
            'channel_title': [channel_snippet['title']],
            'channel_description': [channel_snippet['description']],
            'topic_categories': [', '.join(topic_details.get('topicCategories', []))],
            'title': [snippet['title']],
            'description': [snippet['description']],
            'tags': [', '.join(snippet.get('tags', []))],
            'thumbnails': [snippet['thumbnails']['default']['url']]
        }

        stats_df = pd.DataFrame(stats_data)
        save_statistics_to_excel(stats_df, STATS_FILE_PATH)

# API 키
API_KEY = ''

# 날짜별 파일 경로
target_date = "2025-05-08"
file_suffix = target_date[2:4] + target_date[5:7] + target_date[8:10]
STATS_FILE_PATH = f'/Users/syb/Desktop/youtube_crawling/data_only_stats/stats_{file_suffix}.xlsx'

# 채널 목록
channel_ids = [
    'UCEX1cZB5TL7jyKejXdTXCKA', 'UCYJDUekoQz0-bo8al1diLWQ', 'UC9ta639M37zzWKwo7kKc80A',
    'UCUj6rrhMTR9pipbAWBAMvUQ', 'UCTx3aCntDvkq-hGtOjKVSnQ', 'UCRWxH4fGhuNsh0klWnDbt0w',
    'UCA6KBBX8cLwYZNepxlE_7SA', 'UCQr8QXrzpIE1yCz7l283rqQ', 'UC98TOxKQk4aLcx0EjIK0LkQ',
    'UCvakL6TODG0Wm1ZFN3y13AA', 'UCC9pQY_uaBSa0WOpMNJHbEQ', 'UCTQGAYPtbnCEfW9IGx65kiw',
    'UCBkyj16n2snkRg1BAzpovXQ', 'UC0VR2v4TZeGcOrZHnmwbU_Q', 'UCmJEpV4hLzGWLU5rrdOHMhQ',
    'UChU7a6tVcJ-PEG4VW0dL36w', 'UC_BVJYxQxN5jberGxAFDlyg', 'UC1OU9eIk4JGnh9DN1yxwW9A',
    'UCyozK5OFN5lDrwim5wqQnLA', 'UCCb6W2FU1L7j9mw14YK-9yg', 'UCyar0OYt0LoPzkkWcQAo6OA',
    'UCRXL4vnST2AE6UtjrYMv4tw', 'UCkEWsTetbUuGDg_O1S5UVBg', 'UCwiSPfQKmSaOJDZrXN_AyAA',
    'UCj-durTg1W7uWsB8oq0u7kA', 'UC8CIM3d3zDYMk-3T5aAz0yw', 'UCaHGOzOyeYzLQeKsVkfLEGA',
    'UCMz9HhOzc6yPcz0HQF6xVtQ', 'UCPvwqht-XvcbbaUavs53ejg', 'UCW-QBEmxsme2FAONSnTjp-g'
]

# 실행
for channel_id in channel_ids:
    extract_latest_videos_from_channel(channel_id, STATS_FILE_PATH)
