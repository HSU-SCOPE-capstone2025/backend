import os
import pandas as pd
import logging

from collections import defaultdict

# 로깅 설정
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")

# 경로 설정
folder_0505 = "/Users/syb/Desktop/data_merge/tiktok/tiktok_influencer_redata0505"
folder_0506 = "/Users/syb/Desktop/data_merge/tiktok/tiktok_influencer_redata0506"
output_folder = "/Users/syb/Desktop/data_merge/tiktok/tiktok_influencer_final"

# 인플루언서 폴더 리스트
influencers = sorted(list(set(os.listdir(folder_0505)) | set(os.listdir(folder_0506))))

for influencer in influencers:
    logging.info(f"📦 병합 중: {influencer}")

    # === stats 병합 ===
    stats_0505_path = os.path.join(folder_0505, influencer, f"redata_stats_{influencer}.xlsx")
    stats_0506_path = os.path.join(folder_0506, influencer, f"redata_stats_{influencer}.xlsx")
    stats_0505 = pd.read_excel(stats_0505_path) if os.path.exists(stats_0505_path) else pd.DataFrame()
    stats_0506 = pd.read_excel(stats_0506_path) if os.path.exists(stats_0506_path) else pd.DataFrame()

    if not stats_0505.empty and not stats_0506.empty:
        merged_stats = pd.concat([stats_0506, stats_0505[~stats_0505["video_url"].isin(stats_0506["video_url"])]], ignore_index=True)
    else:
        merged_stats = stats_0506 if not stats_0506.empty else stats_0505

    # === comments 병합 ===
    comments_0505_path = os.path.join(folder_0505, influencer, f"redata_comments_{influencer}.xlsx")
    comments_0506_path = os.path.join(folder_0506, influencer, f"redata_comments_{influencer}.xlsx")
    comments_0505 = pd.read_excel(comments_0505_path) if os.path.exists(comments_0505_path) else pd.DataFrame()
    comments_0506 = pd.read_excel(comments_0506_path) if os.path.exists(comments_0506_path) else pd.DataFrame()

    if not comments_0505.empty and not comments_0506.empty:
        grouped_0505 = comments_0505.groupby("video_url")
        grouped_0506 = comments_0506.groupby("video_url")
        combined_comments = []

        all_video_urls = set(comments_0505["video_url"]).union(comments_0506["video_url"])
        for video_url in all_video_urls:
            df_0505 = grouped_0505.get_group(video_url) if video_url in grouped_0505.groups else pd.DataFrame()
            df_0506 = grouped_0506.get_group(video_url) if video_url in grouped_0506.groups else pd.DataFrame()
            if len(df_0505) >= len(df_0506):
                combined_comments.append(df_0505)
            else:
                combined_comments.append(df_0506)
        merged_comments = pd.concat(combined_comments, ignore_index=True)
    else:
        merged_comments = comments_0506 if not comments_0506.empty else comments_0505

    # === 저장 ===
    save_dir = os.path.join(output_folder, influencer)
    os.makedirs(save_dir, exist_ok=True)
    stats_save_path = os.path.join(save_dir, f"redata_stats_{influencer}.xlsx")
    comments_save_path = os.path.join(save_dir, f"redata_comments_{influencer}.xlsx")

    merged_stats.to_excel(stats_save_path, index=False)
    merged_comments.to_excel(comments_save_path, index=False)

    logging.info(f"✅ 저장 완료: {influencer}")