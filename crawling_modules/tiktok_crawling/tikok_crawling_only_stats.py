import os
import time
import logging
import warnings
import re
import pandas as pd
import undetected_chromedriver as uc
from datetime import datetime, timedelta
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# 로깅 및 경고 설정
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logging.getLogger("urllib3.connectionpool").setLevel(logging.ERROR)
warnings.filterwarnings('ignore')

# === 드라이버 초기화 ===
def initialize_driver():
    options = uc.ChromeOptions()
    user_data_dir = os.path.join(os.getcwd(), "chrome_user_data")
    os.makedirs(user_data_dir, exist_ok=True)
    options.add_argument(f"--user-data-dir={user_data_dir}")
    options.add_argument("--window-size=1200,800")
    options.add_argument("--disable-popup-blocking")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-blink-features=AutomationControlled")
    options.add_experimental_option("prefs", {
        "profile.default_content_setting_values.notifications": 1
    })
    driver = uc.Chrome(options=options, enable_cdp_events=True)
    driver.implicitly_wait(5)
    return driver

# === 상대 날짜 파싱 ===
def parse_relative_date(rel_date_str, current_date=None):
    if current_date is None:
        current_date = datetime.now()
    rel_date_str = rel_date_str.strip()
    if re.match(r'^\d{1,2}-\d{1,2}$', rel_date_str):
        month, day = map(int, rel_date_str.split('-'))
        return f"{current_date.year:04d}-{month:02d}-{day:02d}"
    if "-" in rel_date_str:
        return rel_date_str
    if "방금" in rel_date_str or "분" in rel_date_str:
        return current_date.strftime("%Y-%m-%d")
    if "시간" in rel_date_str:
        hours = int("".join(filter(str.isdigit, rel_date_str))) or 1
        return (current_date - timedelta(hours=hours)).strftime("%Y-%m-%d")
    if "일" in rel_date_str:
        days = int("".join(filter(str.isdigit, rel_date_str))) or 1
        return (current_date - timedelta(days=days)).strftime("%Y-%m-%d")
    if "주" in rel_date_str:
        weeks = int("".join(filter(str.isdigit, rel_date_str))) or 1
        return (current_date - timedelta(weeks=weeks)).strftime("%Y-%m-%d")
    if "개월" in rel_date_str:
        months = int("".join(filter(str.isdigit, rel_date_str))) or 1
        return (current_date - timedelta(days=30 * months)).strftime("%Y-%m-%d")
    if "년" in rel_date_str:
        years = int("".join(filter(str.isdigit, rel_date_str))) or 1
        return (current_date - timedelta(days=365 * years)).strftime("%Y-%m-%d")
    return current_date.strftime("%Y-%m-%d")

# === 숫자 변환 함수 ===
def convert_to_number(text):
    try:
        if pd.isna(text): return 0
        text = str(text).lower().replace(",", "").strip()
        if 'k' in text: return int(float(text.replace('k', '')) * 1000)
        if 'm' in text: return int(float(text.replace('m', '')) * 1000000)
        return int(float(text))
    except: return 0

# === 썸네일에서 video_url + view_count, follower 수 수집 ===
def get_latest_videos(driver, user_profile_url, max_results=50):
    logging.info("프로필 페이지 로드: %s", user_profile_url)
    driver.get(user_profile_url)
    try:
        follower_elem = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.XPATH, "//strong[@data-e2e='followers-count']"))
        )
        follower_num = convert_to_number(follower_elem.text)
    except:
        follower_num = 0

    video_urls = []
    view_dict = {}
    scroll_attempts = 0

    while len(video_urls) < max_results and scroll_attempts < 5:
        cards = driver.find_elements(By.XPATH, "//div[@data-e2e='user-post-item']//a[contains(@href, '/video/')]")
        for card in cards:
            try:
                url = card.get_attribute("href").split('?')[0]
                view_elem = card.find_element(By.XPATH, ".//strong")
                view_count = convert_to_number(view_elem.text)
                if url not in video_urls:
                    video_urls.append(url)
                    view_dict[url] = view_count
            except: continue
            if len(video_urls) >= max_results:
                break
        driver.execute_script("window.scrollBy(0, 2000);")
        time.sleep(1.2)
        scroll_attempts += 1

    logging.info("수집된 동영상 개수: %d", len(video_urls))
    return video_urls[:max_results], view_dict, follower_num

# === 동영상 정보 수집 ===
def get_video_info(driver, video_url, view_dict=None, follower_num=None):
    logging.info("동영상 정보 수집: %s", video_url)
    driver.get(video_url)
    time.sleep(3)

    try:
        driver.find_element(By.XPATH, "//button[contains(text(), '더보기')]").click()
        time.sleep(1)
    except: pass

    try:
        desc_elem = driver.find_element(By.XPATH, "//h1[contains(@data-e2e, 'video-desc')]")
        description = desc_elem.text.strip()
    except: description = ""

    try:
        like_elem = driver.find_element(By.XPATH, "//strong[@data-e2e='like-count']")
        like_count = convert_to_number(like_elem.text.strip())
    except: like_count = 0

    try:
        comment_elem = driver.find_element(By.XPATH, "//strong[@data-e2e='comment-count']")
        comment_count = comment_elem.text.strip()
    except: comment_count = "0"

    try:
        share_elem = driver.find_element(By.XPATH, "//strong[@data-e2e='share-count']")
        raw_share = share_elem.text.strip()
        share_count = 0 if raw_share == "공유" else convert_to_number(raw_share)
    except: share_count = 0

    try:
        user_elem = driver.find_element(By.XPATH, "//h2[contains(@data-e2e, 'user-title')]")
        channel_title = user_elem.text.strip()
    except: channel_title = ""

    try:
        date_elem = driver.find_element(By.XPATH, "//span[@data-e2e='browser-nickname']/span[last()]")
        upload_date = parse_relative_date(date_elem.text.strip())
    except:
        upload_date = datetime.now().strftime('%Y-%m-%d')

    view_count = view_dict.get(video_url, 0)

    return {
        "video_url": video_url,
        "description": description,
        "like_count": like_count,
        "comment_count": comment_count,
        "share_count": share_count,
        "channel_title": channel_title,
        "upload_date": upload_date,
        "view_count": view_count,
        "follower_num": follower_num if follower_num is not None else 0
    }

# === 동영상 저장 ===
def extract_latest_videos_from_channel(driver, user_profile_url, stats_file_path, max_results=30):
    logging.info("프로필에서 동영상 추출 시작: %s", user_profile_url)
    video_urls, view_dict, follower_num = get_latest_videos(driver, user_profile_url, max_results)

    for idx, video_url in enumerate(video_urls):
        try:
            video_info = get_video_info(driver, video_url, view_dict, follower_num)
            stats_df = pd.DataFrame([video_info])
            if os.path.exists(stats_file_path):
                existing_stats_df = pd.read_excel(stats_file_path)
                combined_stats_df = pd.concat([existing_stats_df, stats_df], ignore_index=True)
            else:
                combined_stats_df = stats_df
            combined_stats_df.to_excel(stats_file_path, index=False)
            logging.info("영상 %d 저장 완료 (%s)", idx + 1, video_url)
        except Exception as e:
            logging.error("동영상 처리 중 오류 발생: %s", e)

# === 메인 ===
if __name__ == "__main__":
    driver = initialize_driver()
    driver.get("https://www.tiktok.com/foryou")
    time.sleep(3)

    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")

    user_profiles = [
        # "https://www.tiktok.com/@__ralral__",
        # "https://www.tiktok.com/@horseking88",
        # "https://www.tiktok.com/@joodoonge",
        # "https://www.tiktok.com/@calmdownman_plus1",
        # "https://www.tiktok.com/@trytoeat222", 
        # "https://www.tiktok.com/@under_world_b1",
        # "https://www.tiktok.com/@heebab.tiktok", 
        # "https://www.tiktok.com/@jihan_ee",
        # "https://www.tiktok.com/@yellow_aquarium",
        "https://www.tiktok.com/@park_esther", 
        "https://www.tiktok.com/@simcook_",
        "https://www.tiktok.com/@minsco_official",
        "https://www.tiktok.com/@zaddnognoaw", 
        "https://www.tiktok.com/@yooxicmanfan1",
        # "https://www.tiktok.com/@dudely_08",
        "https://www.tiktok.com/@fundamental_kr", 
        "https://www.tiktok.com/@lesuho_",
        "https://www.tiktok.com/@reneirubinr2",
        "https://www.tiktok.com/@yonamism",
        "https://www.tiktok.com/@mejoocats", 
        "https://www.tiktok.com/@jeon_unni",
        "https://www.tiktok.com/@bboyongnyong",
        "https://www.tiktok.com/@salim_nam_official", 
        "https://www.tiktok.com/@enjoycouple",
        "https://www.tiktok.com/@hxxax__", 
        "https://www.tiktok.com/@g_movie_official",
        "https://www.tiktok.com/@daldalsisters", 
        "https://www.tiktok.com/@ipduck_official",
        "https://www.tiktok.com/@baby_pig_rabbit"
    ]

    for profile_url in user_profiles:
        influencer_name = profile_url.rstrip('/').split('@')[-1]
        stats_file_path = f"/Users/syb/Desktop/tiktok_crawling/tiktok_stats_only/tiktok_stats_{influencer_name}_{timestamp}.xlsx"
        extract_latest_videos_from_channel(
            driver=driver,
            user_profile_url=profile_url,
            stats_file_path=stats_file_path,
            max_results=7
        )

    driver.quit()