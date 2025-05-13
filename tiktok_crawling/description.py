import os
import pandas as pd
import re
import time
import logging
import undetected_chromedriver as uc
from datetime import datetime
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# 로깅 설정
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")

# === 셀레니움 드라이버 초기화 ===
def initialize_driver():
    options = uc.ChromeOptions()
    user_data_dir = os.path.join(os.getcwd(), "chrome_user_data")
    options.add_argument(f"--user-data-dir={user_data_dir}")
    options.add_argument("--window-size=750,800")
    options.add_argument("--disable-blink-features=AutomationControlled")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-popup-blocking")
    driver = uc.Chrome(options=options, enable_cdp_events=True)
    driver.implicitly_wait(5)
    return driver

# === 설명 추출 함수 ===
def get_description(driver, video_url):
    try:
        driver.get(video_url)
        time.sleep(3)

        # 1. 기본 방법: 브라우저 title 이용
        description = driver.title.replace(" | TikTok", "").strip()

        # 2. 보완: 실제 설명 영역 XPath
        if not description:
            desc_xpath = "//div[contains(@class, 'DivDescriptionWrapper')]"
            desc_elem = WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.XPATH, desc_xpath))
            )
            description = desc_elem.text.strip()

        return description

    except Exception as e:
        logging.warning(f"[❌ description 추출 실패] {video_url} → {e}")
        return ""

# === description만 업데이트 ===
def update_descriptions(driver, input_base_folder):
    for influencer in os.listdir(input_base_folder):
        influencer_path = os.path.join(input_base_folder, influencer)
        if not os.path.isdir(influencer_path):
            continue

        file_path = os.path.join(influencer_path, f"redata_stats_{influencer}.xlsx")
        if not os.path.exists(file_path):
            continue

        df = pd.read_excel(file_path)
        if "video_url" not in df.columns:
            logging.warning(f"⚠️ video_url 컬럼 없음: {file_path}")
            continue

        if "description" not in df.columns:
            df["description"] = ""

        logging.info(f"▶ {influencer} 영상 {len(df)}개 description 업데이트 시작")

        for idx, row in df.iterrows():
            if pd.isna(row["description"]) or not str(row["description"]).strip():
                desc = get_description(driver, row["video_url"])
                df.at[idx, "description"] = desc
                logging.info(f"  ▷ [{idx+1}/{len(df)}] {desc[:50]}...")

        # ✅ 덮어쓰기 저장
        df.to_excel(file_path, index=False)
        logging.info(f"✅ 덮어쓰기 저장 완료: {file_path}")

# === 실행 ===
if __name__ == "__main__":
    FINAL_FOLDER = "/Users/syb/Desktop/data_merge/tiktok/tiktok_influencer_final"

    driver = initialize_driver()
    logging.info("💻 TikTok description 재수집 시작")

    update_descriptions(driver, FINAL_FOLDER)

    driver.quit()
    logging.info("🎉 전체 description 업데이트 완료!")