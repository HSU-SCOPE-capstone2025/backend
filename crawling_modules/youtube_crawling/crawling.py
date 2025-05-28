# 댓글 데이터까지 저장하는 코드

import pandas as pd
from googleapiclient.discovery import build
import warnings
from datetime import datetime
import re  # 추가: 정규표현식 사용

# 경고 메시지 무시
warnings.filterwarnings('ignore')

# 추가: Excel에서 허용하지 않는 제어 문자를 제거하는 함수
def clean_illegal_characters(text):
    """
    Excel에 저장 시 허용하지 않는 제어 문자들을 제거합니다.
    (\x00-\x08, \x0B-\x0C, \x0E-\x1F)
    """
    if isinstance(text, str):
        return re.sub(r'[\x00-\x08\x0B-\x0C\x0E-\x1F]', '', text)
    return text

# 추가: DataFrame의 문자열 컬럼에 대해 불법 문자를 제거하는 함수
def clean_dataframe_strings(df):
    for col in df.select_dtypes(include=[object]).columns:
        df[col] = df[col].apply(clean_illegal_characters)
    return df

# YouTube API 초기화
def initialize_api(api_key):
    return build('youtube', 'v3', developerKey=api_key)

# 영상 정보 가져오기
def get_video_info(api_obj, video_id):
    response = api_obj.videos().list(
        part='snippet,statistics,topicDetails',
        id=video_id
    ).execute()
    video_info = response['items'][0]
    snippet = video_info['snippet']
    statistics = video_info['statistics']
    topic_details = video_info.get('topicDetails', {})  # topicDetails가 없을 수도 있음
    return snippet, statistics, topic_details

# 통계 정보를 Excel 파일에 저장
def save_statistics_to_excel(stats_df, file_path):
    # 수정: 저장 전에 DataFrame의 문자열 내 불법 문자 제거
    stats_df = clean_dataframe_strings(stats_df)
    try:
        existing_stats_df = pd.read_excel(file_path)
        stats_df = pd.concat([existing_stats_df, stats_df], ignore_index=True)
    except FileNotFoundError:
        pass
    stats_df.to_excel(file_path, index=False)
    print(f"통계 정보를 {file_path}에 저장했습니다.")

# 댓글 추출 (최대 max_comments개, target_date에 해당하는 댓글만 필터링)
def extract_video_comments(api_obj, video_id, max_comments=1000000, target_date=None):
    comments = []
    response = api_obj.commentThreads().list(
        part='snippet,replies',
        videoId=video_id,
        maxResults=100  # 한 페이지당 최대 100개
    ).execute()
    
    while response and len(comments) < max_comments:
        for item in response.get('items', []):
            if len(comments) >= max_comments:
                break
            # 최상위 댓글 추출
            comment = item['snippet']['topLevelComment']['snippet']
            comment_date = datetime.strptime(comment['publishedAt'], '%Y-%m-%dT%H:%M:%SZ').strftime('%Y-%m-%d')
            comments.append([
                comment['textDisplay'],
                comment['authorDisplayName'],
                comment_date,
                comment['likeCount'],
                video_id
            ])
            
            if len(comments) >= max_comments:
                break

            # 대댓글(답글) 추출 (있을 경우)
            if item['snippet'].get('totalReplyCount', 0) > 0 and 'replies' in item:
                for reply_item in item['replies']['comments']:
                    if len(comments) >= max_comments:
                        break
                    reply = reply_item['snippet']
                    reply_date = datetime.strptime(reply['publishedAt'], '%Y-%m-%dT%H:%M:%SZ').strftime('%Y-%m-%d')
                    comments.append([
                        reply['textDisplay'],
                        reply['authorDisplayName'],
                        reply_date,
                        reply['likeCount'],
                        video_id
                    ])
        
        if len(comments) >= max_comments:
            break
        
        if 'nextPageToken' in response:
            response = api_obj.commentThreads().list(
                part='snippet,replies',
                videoId=video_id,
                pageToken=response['nextPageToken'],
                maxResults=100
            ).execute()
        else:
            break
    
    comments_df = pd.DataFrame(comments, columns=['comment', 'author', 'date', 'num_likes', 'video_id'])
    if target_date:
        # 수정: target_date가 지정되면 해당 날짜와 일치하는 댓글만 필터링
        filtered_df = comments_df[comments_df['date'] == target_date]
        print(f"Filtered 댓글 개수 for {target_date}: {len(filtered_df)}")
        comments_df = filtered_df
    return comments_df

# 댓글 정보를 Excel 파일에 저장
def save_comments_to_excel(comments_df, file_path):
    # 수정: 저장 전에 DataFrame의 문자열 내 불법 문자 제거
    comments_df = clean_dataframe_strings(comments_df)
    try:
        existing_comments_df = pd.read_excel(file_path)
        comments_df = pd.concat([existing_comments_df, comments_df], ignore_index=True)
    except FileNotFoundError:
        pass  # 파일이 없으면 새로 저장
    comments_df.to_excel(file_path, index=False)
    print(f"댓글을 {file_path}에 추가 또는 저장했습니다.")

# 채널 정보 가져오기
def get_channel_info(api_obj, channel_id):
    response = api_obj.channels().list(
        part='snippet,statistics',
        id=channel_id
    ).execute()
    channel_info = response['items'][0]
    snippet = channel_info['snippet']
    statistics = channel_info['statistics']
    return snippet, statistics

# 채널의 최신 영상 Video ID 5개 가져오기 -- 수정 필요! (여기선 max_results 기본값은 그대로 사용)
def get_latest_videos(api_obj, channel_id, max_results=10):
    response = api_obj.search().list(
        part='snippet',
        channelId=channel_id,
        order='date',       # 최신순 정렬
        maxResults=max_results,
        type='video'        # 영상만 검색
    ).execute()

    video_ids = []
    for item in response.get('items', []):
        video_id = item['id'].get('videoId')
        if video_id:
            video_ids.append(video_id)
    return video_ids

# 채널 ID를 이용하여 최신 영상 7개씩 데이터 추출 및 저장 (target_date에 해당하는 댓글만 저장)
def extract_latest_videos_from_channel(channel_id, STATS_FILE_PATH, COMMENTS_FILE_PATH, target_date=None):
    # API 초기화
    api_obj = initialize_api(API_KEY)
    
    # 최신 영상 5개 Video ID 가져오기 -> 7개로 변경
    video_ids = get_latest_videos(api_obj, channel_id, max_results=7)
    
    for video_id in video_ids:
        # 영상 정보 추출
        snippet, statistics, topic_details = get_video_info(api_obj, video_id)
        current_date = datetime.now().strftime('%Y-%m-%d')
        # 업로드 날짜를 YYYY-MM-DD 형식으로 변환
        upload_date = datetime.strptime(snippet['publishedAt'], '%Y-%m-%dT%H:%M:%SZ').strftime('%Y-%m-%d')
    
        # 채널 정보 추출 (채널 ID가 있으므로 동일하게 사용)
        channel_snippet, channel_statistics = get_channel_info(api_obj, channel_id)
        channel_title = channel_snippet['title']
        channel_description = channel_snippet['description']
        subscriber_count = channel_statistics.get('subscriberCount', 0)
        
        # 통계 정보를 DataFrame에 정리
        stats_data = {
            'video_id': video_id,
            'upload_date': [upload_date],
            'date': [current_date],
            'view_count': [statistics.get('viewCount')],
            'like_count': [statistics.get('likeCount')],
            'comment_count': [statistics.get('commentCount')],
            'subscriber_count': [subscriber_count],
            'channel_id': [channel_id],
            'channel_title': [channel_title],
            'channel_description': [channel_description],
            'topic_categories': [', '.join(topic_details.get('topicCategories', []))],
            'title': [snippet['title']],
            'description': [snippet['description']],
            'tags': [', '.join(snippet.get('tags', []))],
            'thumbnails': [snippet['thumbnails']['default']['url']]
        }
        stats_df = pd.DataFrame(stats_data)
        
        # 통계 정보를 Excel에 저장
        save_statistics_to_excel(stats_df, STATS_FILE_PATH)
        
        # 댓글 추출 시 target_date를 전달하여 해당 날짜의 댓글만 필터링
        comments_df = extract_video_comments(api_obj, video_id, target_date=target_date)
        save_comments_to_excel(comments_df, COMMENTS_FILE_PATH)

# API 키 (본인의 API 키로 대체하세요)
API_KEY = ''

# target_date 설정 (예: "2025-02-23")  <-- 수정: 날짜별 데이터 저장을 위해 target_date 지정
target_date = "2025-05-05"
file_suffix = target_date[2:4] + target_date[5:7] + target_date[8:10]

# 저장할 파일 경로 (날짜별로 파일명을 구성)  <-- 수정: 파일명에 target_date를 반영
STATS_FILE_PATH = f'/Users/syb/Desktop/youtube_crawling/data_stats/stats_{file_suffix}.xlsx'
COMMENTS_FILE_PATH = f'/Users/syb/Desktop/youtube_crawling/data_crawling_auto/crawling_auto_{file_suffix}.xlsx'

# 분석할 유튜버(채널)의 채널 ID 리스트
channel_ids = [
    'UCEX1cZB5TL7jyKejXdTXCKA', # 랄랄ralral
    'UCYJDUekoQz0-bo8al1diLWQ', # 말왕TV
    'UC9ta639M37zzWKwo7kKc80A', # 주둥이방송
    'UCUj6rrhMTR9pipbAWBAMvUQ', # 침착맨
    'UCTx3aCntDvkq-hGtOjKVSnQ', # 먹어볼래TryToEat
    'UCRWxH4fGhuNsh0klWnDbt0w', # 언더월드 UNDER WORLD
    'UCA6KBBX8cLwYZNepxlE_7SA', # 히밥heebab
    'UCQr8QXrzpIE1yCz7l283rqQ', # 지하니
    'UC98TOxKQk4aLcx0EjIK0LkQ', # 해수인tv yellow aquarium
    'UCvakL6TODG0Wm1ZFN3y13AA', # 박에스더 - PARK ESTHER
    'UCC9pQY_uaBSa0WOpMNJHbEQ', # 자취요리신 simple cooking
    'UCTQGAYPtbnCEfW9IGx65kiw', # Minsco 민스코
    'UCBkyj16n2snkRg1BAzpovXQ', # 우왁굳
    'UC0VR2v4TZeGcOrZHnmwbU_Q', # 육식맨 YOOXICMAN
    'UCmJEpV4hLzGWLU5rrdOHMhQ', # 더들리
    'UChU7a6tVcJ-PEG4VW0dL36w', # 뻔더
    'UC_BVJYxQxN5jberGxAFDlyg', # 은수저
    'UC1OU9eIk4JGnh9DN1yxwW9A', # 인생 녹음 중
    'UCyozK5OFN5lDrwim5wqQnLA', # 취미로 요리하는 남자 Yonam
    'UCCb6W2FU1L7j9mw14YK-9yg', # 김메주와 고양이들
    'UCyar0OYt0LoPzkkWcQAo6OA', # 젼언니 jeon_unni
    'UCRXL4vnST2AE6UtjrYMv4tw', # 뽀용뇽ㅣ롱+숏,골라먹는 뷰티 꿀팁
    'UCkEWsTetbUuGDg_O1S5UVBg', # 살림남 The Life
    'UCwiSPfQKmSaOJDZrXN_AyAA', # 요리하는 민사장
    'UCj-durTg1W7uWsB8oq0u7kA', # 엔조이커플enjoycouple
    'UC8CIM3d3zDYMk-3T5aAz0yw', # 혜안
    'UCaHGOzOyeYzLQeKsVkfLEGA', # 지무비 : G Movie
    'UCMz9HhOzc6yPcz0HQF6xVtQ', # 달달투어
    'UCPvwqht-XvcbbaUavs53ejg', # 입시덕후
    'UCW-QBEmxsme2FAONSnTjp-g'  # 돼끼 baby_pig_rabbit
]

# 각 채널에 대해 최신 영상 7개씩 데이터 추출 실행 (target_date를 전달)
for channel_id in channel_ids:
    extract_latest_videos_from_channel(channel_id, STATS_FILE_PATH, COMMENTS_FILE_PATH, target_date=target_date)