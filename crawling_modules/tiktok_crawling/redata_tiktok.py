import os
import pandas as pd
import re
import time
import logging
import warnings

from datetime import datetime, timedelta
import undetected_chromedriver as uc
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

warnings.filterwarnings("ignore")
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")

def initialize_driver():
    options = uc.ChromeOptions()
    user_data_dir = os.path.join(os.getcwd(), "chrome_user_data")
    options.add_argument(f"--user-data-dir=" + user_data_dir)
    options.add_argument("--window-size=750,800")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-popup-blocking")
    options.add_argument("--disable-blink-features=AutomationControlled")
    driver = uc.Chrome(options=options, enable_cdp_events=True)
    driver.implicitly_wait(5)
    driver.set_window_size(750, 800)
    return driver

def parse_relative_date(date_str, base_date):
    date_str = str(date_str).strip()
    if "방금" in date_str or "초" in date_str or "분" in date_str:
        return base_date.strftime("%Y-%m-%d")
    if "시간" in date_str:
        hours = int("".join(filter(str.isdigit, date_str))) if any(c.isdigit() for c in date_str) else 1
        return (base_date - timedelta(hours=hours)).strftime("%Y-%m-%d")
    if "일" in date_str:
        days = int("".join(filter(str.isdigit, date_str))) if any(c.isdigit() for c in date_str) else 1
        return (base_date - timedelta(days=days)).strftime("%Y-%m-%d")
    if "주" in date_str:
        weeks = int("".join(filter(str.isdigit, date_str))) if any(c.isdigit() for c in date_str) else 1
        return (base_date - timedelta(weeks=weeks)).strftime("%Y-%m-%d")
    if "개월" in date_str:
        months = int("".join(filter(str.isdigit, date_str))) if any(c.isdigit() for c in date_str) else 1
        return (base_date - timedelta(days=30 * months)).strftime("%Y-%m-%d")
    if "년" in date_str:
        years = int("".join(filter(str.isdigit, date_str))) if any(c.isdigit() for c in date_str) else 1
        return (base_date - timedelta(days=365 * years)).strftime("%Y-%m-%d")
    return date_str

def fix_comment_date(date_str, base_date):
    if pd.isna(date_str):
        return date_str
    date_str = str(date_str).strip()
    if re.match(r"^\d{1,2}-\d{1,2}$", date_str):
        month, day = map(int, date_str.split('-'))
        return f"2025-{month:02d}-{day:02d}"
    return parse_relative_date(date_str, base_date)

def process_comment_files(input_folder, output_base_folder):
    for file in os.listdir(input_folder):
        if file.endswith(".xlsx") and "comments" in file:
            input_path = os.path.join(input_folder, file)
            df = pd.read_excel(input_path)

            influencer_name = file.split('_')[2]
            match = re.search(r"_(\d{8})_", file)
            if not match:
                logging.warning(f"❌ 날짜 패턴을 파일명에서 찾을 수 없음: {file}")
                continue
            base_date_str = match.group(1)
            base_date = datetime.strptime(base_date_str, "%Y%m%d")

            logging.info(f"▶ 댓글 파일 처리 중: {file} ({len(df)}개), 기준일자: {base_date.strftime('%Y-%m-%d')})")

            df["date"] = df["date"].apply(lambda x: fix_comment_date(x, base_date))

            influencer_folder = os.path.join(output_base_folder, influencer_name)
            os.makedirs(influencer_folder, exist_ok=True)
            output_path = os.path.join(influencer_folder, f"redata_comments_{influencer_name}_{datetime.now().strftime('%Y%m%d')}.xlsx")
            df.to_excel(output_path, index=False)
            logging.info(f"✅ 댓글 저장 완료: {output_path}")

def reextract_video_info(driver, video_url):
    driver.get(video_url)
    time.sleep(3)

    def get_text_by_xpath(xpath, timeout=20):
        try:
            element = WebDriverWait(driver, timeout).until(
                EC.presence_of_element_located((By.XPATH, xpath))
            )
            return element.text.strip()
        except:
            return ""

    description = driver.title.replace(" | TikTok", "").strip()
    if not description:
        description = get_text_by_xpath("//div[contains(@class, 'DivDescriptionWrapper')]")

    # upload_date_raw = get_text_by_xpath("//div[@data-e2e='browser-nickname']/span[last()]")
    # upload_date_raw = get_text_by_xpath("//a[contains(@href, '/@')]/following-sibling::span")
    # upload_date_raw = get_text_by_xpath("//a[contains(@href, '/@')]/span")
    upload_date_raw = get_text_by_xpath("(//a[contains(@href, '/@')]/span)[last()]")
    if not upload_date_raw or upload_date_raw.strip() == "":
        upload_date_raw = get_text_by_xpath("(//span[contains(text(), '-')])[last()]")

    upload_date = ""
    try:
        if not upload_date_raw or upload_date_raw.strip() == "":
            logging.warning(f"[업로드일자 없음] {video_url}")
            upload_date = "not available"
        elif re.match(r"^\d{4}-\d{1,2}-\d{1,2}$", upload_date_raw):
            upload_date = upload_date_raw
        elif re.match(r"^\d{1,2}-\d{1,2}$", upload_date_raw):
            month, day = map(int, upload_date_raw.split('-'))
            upload_date = f"2025-{month:02d}-{day:02d}"
        else:
            upload_date = upload_date_raw
    except Exception as e:
        logging.warning(f"[업로드일자 파싱 실패] {video_url} → {e}")
        upload_date = "parse error"

    view_count = get_text_by_xpath("//strong[@data-e2e='view-count']")
    share_count = get_text_by_xpath("//strong[@data-e2e='share-count']")
    if share_count == "공유":
        share_count = "0"

    return {
        "video_url": video_url,
        "description": description,
        "upload_date": upload_date,
        "view_count": view_count,
        "share_count": share_count
    }

def reprocess_video_stats(driver, input_folder, output_base_folder):
    for file in os.listdir(input_folder):
        if file.endswith(".xlsx") and "stats" in file:
            input_path = os.path.join(input_folder, file)
            df = pd.read_excel(input_path)
            influencer_name = file.split('_')[2]
            logging.info(f"▶ 영상 파일 처리 중: {file} ({len(df)}개)")

            new_data = []
            for idx, row in df.iterrows():
                try:
                    video_info = reextract_video_info(driver, row["video_url"])
                    logging.info(f"""
[{idx+1}/{len(df)}] 수집 완료
📹 video_url: {video_info['video_url']}
📝 description: {video_info['description']}
📅 upload_date: {video_info['upload_date']}
👁️ view_count: {video_info['view_count']}
🔁 share_count: {video_info['share_count']}
""")
                    new_data.append(video_info)
                except Exception as e:
                    logging.warning(f"❌ {row['video_url']} 오류: {e}")

            new_df = pd.DataFrame(new_data)
            influencer_folder = os.path.join(output_base_folder, influencer_name)
            os.makedirs(influencer_folder, exist_ok=True)
            output_path = os.path.join(influencer_folder, f"redata_stats_{influencer_name}_{datetime.now().strftime('%Y%m%d')}.xlsx")
            new_df.to_excel(output_path, index=False)
            logging.info(f"✅ 영상 저장 완료: {output_path}")

if __name__ == "__main__":
    STATS_FOLDER = "/Users/syb/Desktop/tiktok_crawling/tiktok_stats"
    COMMENTS_FOLDER = "/Users/syb/Desktop/tiktok_crawling/tiktok_comments"
    OUTPUT_FOLDER = "/Users/syb/Desktop/tiktok_crawling/tiktok_influencer_redata"

    driver = initialize_driver()
    logging.info("========== 댓글 파일 처리 시작 ==========")
    process_comment_files(COMMENTS_FOLDER, OUTPUT_FOLDER)

    logging.info("========== 영상 정보 재수집 시작 ==========")
    reprocess_video_stats(driver, STATS_FOLDER, OUTPUT_FOLDER)

    driver.quit()
    logging.info("🎉 모든 작업 완료!")