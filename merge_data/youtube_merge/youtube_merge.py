import os
import pandas as pd
from datetime import datetime, timedelta

# 타겟 인플루언서 채널 ID
target_channels = {
    'UCEX1cZB5TL7jyKejXdTXCKA': '랄랄',
    'UCYJDUekoQz0-bo8al1diLWQ': '말왕',
    'UC9ta639M37zzWKwo7kKc80A': '주둥이방송',
    'UCUj6rrhMTR9pipbAWBAMvUQ': '침착맨',
    'UCTx3aCntDvkq-hGtOjKVSnQ': '먹어볼래 TryToEat',
    'UCRWxH4fGhuNsh0klWnDbt0w': '언더월드',
    'UCA6KBBX8cLwYZNepxlE_7SA': '히밥',
    'UCQr8QXrzpIE1yCz7l283rqQ': '지한',
    'UC98TOxKQk4aLcx0EjIK0LkQ': '해수인',
    'UCvakL6TODG0Wm1ZFN3y13AA': '박에스더',
    'UCC9pQY_uaBSa0WOpMNJHbEQ': '자취요리신',
    'UCTQGAYPtbnCEfW9IGx65kiw': '민스코',
    'UCBkyj16n2snkRg1BAzpovXQ': '우왁굳',
    'UC0VR2v4TZeGcOrZHnmwbU_Q': '육식맨',
    'UCmJEpV4hLzGWLU5rrdOHMhQ': '더들리',
    'UChU7a6tVcJ-PEG4VW0dL36w': '뻔더',
    'UC_BVJYxQxN5jberGxAFDlyg': '은수저',
    'UC1OU9eIk4JGnh9DN1yxwW9A': '인생 녹음 중',
    'UCyozK5OFN5lDrwim5wqQnLA': '취미로 요리하는 남자',
    'UCCb6W2FU1L7j9mw14YK-9yg': '김메주와 고양이들',
    'UCyar0OYt0LoPzkkWcQAo6OA': '전언니',
    'UCRXL4vnST2AE6UtjrYMv4tw': '뽀용뇽',
    'UCkEWsTetbUuGDg_O1S5UVBg': '살림남',
    'UCwiSPfQKmSaOJDZrXN_AyAA': '요리하는 민사장',
    'UCj-durTg1W7uWsB8oq0u7kA': '엔조이커플',
    'UC8CIM3d3zDYMk-3T5aAz0yw': '혜안',
    'UCaHGOzOyeYzLQeKsVkfLEGA': '지무비',
    'UCMz9HhOzc6yPcz0HQF6xVtQ': '달달투어',
    'UCPvwqht-XvcbbaUavs53ejg': '입시덕후',
    'UCW-QBEmxsme2FAONSnTjp-g': '돼끼 baby_pig_rabbit',
}

# 날짜 범위
start_date = datetime.strptime("2025-02-23", "%Y-%m-%d")
end_date = datetime.strptime("2025-05-06", "%Y-%m-%d")

# 파일 경로
STATS_DIR = '/Users/syb/Desktop/youtube_crawling/data_stats'
COMMENTS_DIR = '/Users/syb/Desktop/youtube_crawling/data_crawling_auto'
SAVE_ROOT = '/Users/syb/Desktop/data_merge'

def load_stats():
    all_stats = []
    for single_date in pd.date_range(start_date, end_date):
        suffix = single_date.strftime("%y%m%d")
        path = os.path.join(STATS_DIR, f"stats_{suffix}.xlsx")
        if os.path.exists(path):
            df = pd.read_excel(path)
            all_stats.append(df)
    if all_stats:
        return pd.concat(all_stats, ignore_index=True)
    return pd.DataFrame()

def load_comments_per_week():
    comments_by_week = {}
    current = start_date
    while current <= end_date:
        week_start = current
        week_end = min(current + timedelta(days=6), end_date)
        temp = []
        for single_date in pd.date_range(week_start, week_end):
            suffix = single_date.strftime("%y%m%d")
            path = os.path.join(COMMENTS_DIR, f"crawling_auto_{suffix}.xlsx")
            if os.path.exists(path):
                df = pd.read_excel(path)
                temp.append(df)
        if temp:
            comments_by_week[(week_start, week_end)] = pd.concat(temp, ignore_index=True)
        current += timedelta(days=7)
    return comments_by_week

def save_data():
    stats_df = load_stats()
    comments_weeks = load_comments_per_week()

    for channel_id, name in target_channels.items():
        save_path = os.path.join(SAVE_ROOT, name)
        os.makedirs(save_path, exist_ok=True)

        # 📊 영상 정보 병합 및 최신 정보로 정제
        stats_filtered = stats_df[stats_df['channel_id'] == channel_id]
        stats_latest = stats_filtered.sort_values('date').drop_duplicates('video_id', keep='last')
        stats_latest.to_excel(os.path.join(save_path, 'video_stats.xlsx'), index=False)

        # 💬 댓글 주차별 병합 및 저장
        for (start, end), comments_df in comments_weeks.items():
            sub_df = comments_df[comments_df['video_id'].isin(stats_latest['video_id'])]
            if not sub_df.empty:
                week_file = f"comments_{start.strftime('%y%m%d')}_{end.strftime('%y%m%d')}.xlsx"
                sub_df.to_excel(os.path.join(save_path, week_file), index=False)

save_data()