import os
import random
import requests
import json
from selenium import webdriver
from datetime import datetime
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.keys import Keys
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.action_chains import ActionChains
from selenium.common.exceptions import TimeoutException

import pandas as pd
import time

#스프링부트 주소
influencer_url = "http://localhost:8080/instagram/influencer"
posts_url = "http://localhost:8080/instagram/posts"
post_details_url = "http://localhost:8080/instagram/post-details"

# 크롤링 실행 날짜
today = datetime.today().strftime('%Y-%m-%d')

# 저장 폴더 설정
base_folder = f"influencer/{today}"
os.makedirs(base_folder, exist_ok=True)

# influencer_update.xlsx에서 인플루언서 URL 목록 가져오기
file_path = "influencer_update.xlsx"  # 현재 폴더에 위치한 파일
df = pd.read_excel(file_path)

# n+2번째 행부터 끝까지 데이터 가져오기
#df = df.iloc[12:]

# 웹드라이버 초기화 및 로그인
driver = webdriver.Chrome(service=Service())
driver.get("https://www.instagram.com/accounts/login/")
time.sleep(random.uniform(3, 6))

# 로그인 정보 입력
id = "tla1503@hansung.ac.kr" #"tla1503@naver.com" # tla1503@hansung.ac.kr
pw = "TLArbqh45!@" #"tlarbqh129!" #TLArbqh45!@
inputs = driver.find_elements(By.TAG_NAME, "input")
inputs[0].send_keys(id)
inputs[1].send_keys(pw)
inputs[1].send_keys("\n")
time.sleep(random.uniform(5, 6))

# "Not now" 버튼 클릭
try:
    WebDriverWait(driver, 10).until(
        EC.element_to_be_clickable((By.XPATH, "//div[text()='Not now' or text()='나중에 하기']"))
    ).click()
except Exception as e:
    print("알림 창을 찾지 못했습니다:", e)


follower_list = []

def normalize(raw):
    if not raw:
        return None
    raw = raw.replace(",", "").strip()
    try:
        if "만" in raw:
            return int(float(raw.replace("만", "")) * 10000)
        else:
            return int(raw)
    except:
        return None

# # 모든 insta_url을 순차적으로 방문하여 크롤링 수행
# for index, row in df.iterrows():
#     profile_url = row["insta_url"]

#     # URL에서 마지막 '/' 제거
#     if profile_url.endswith('/'):
#         profile_url = profile_url[:-1]

#     # 인플루언서 ID 추출
#     influencer_name = profile_url.replace("https://www.instagram.com/", "")

#     file_name = f"{base_folder}/influencers_list/influencers_list_{today}.xlsx"

#     driver.get(profile_url)
#     time.sleep(random.uniform(3, 6))

#     try:
#         follower_elem = WebDriverWait(driver, 10).until(
#         EC.presence_of_element_located((By.CSS_SELECTOR, "li:nth-child(2) div a span.html-span.xdj266r"))
#         )
#         follower_text = follower_elem.text.strip()

#         normalized_count = normalize(follower_text)

#         print(normalized_count)

#         # 리스트에 추가
#         follower_list.append({
#             "influencer": influencer_name,
#             "follower_number": normalized_count,
#             "at_time": today
#         })

#         res = requests.post(influencer_url, json={
#             "influencer": influencer_name,
#             "follower_number": normalized_count,
#             "at_time": today
#         })
#         print(f"[POST] {influencer_name}: {res.status_code}")

#     except Exception as e:
#         print(f"{influencer_name} 크롤링 중 오류 발생:", e)

#     # DataFrame 생성
# df_follower = pd.DataFrame(follower_list)

# # 폴더 없으면 생성
# os.makedirs(f"influencers_list", exist_ok=True)

# # 엑셀 저장
# file_name = f"influencers_list/influencers_list_{today}.xlsx"
# df_follower.to_excel(file_name, index=False)

# print(f"팔로워 수 엑셀 저장 완료: {file_name}")


#  게시글 데이터 전송 함수
def send_post_data(post_data):
    headers = {'Content-Type': 'application/json'}
    response = requests.post(posts_url, headers=headers, data=json.dumps(post_data))
    if response.status_code == 200:
        print(f"성공 [POST SUCCESS] - 게시글: {post_data['url']}")
        return response.json().get("postId")  # Spring Boot에서 저장된 post ID 리턴받기
    else:
        print(f"실패 [POST FAIL] - 게시글: {post_data['url']} - Status: {response.status_code}")
        return None

#  댓글 데이터 전송 함수
def send_post_detail_data(detail_data):
    headers = {'Content-Type': 'application/json'}
    response = requests.post(post_details_url, headers=headers, data=json.dumps(detail_data))
    if response.status_code == 200:
        print(f"성공 [POST SUCCESS] - 댓글 작성자: {detail_data['user_name']}")
    else:
        print(f"실패 [POST FAIL] - 댓글 작성자: {detail_data['user_name']} - Status: {response.status_code}")


# 모든 insta_url을 순차적으로 방문하여 크롤링 수행
for index, row in df.iterrows():
    profile_url = row["insta_url"]

    # URL에서 마지막 '/' 제거
    if profile_url.endswith('/'):
        profile_url = profile_url[:-1]

    # 인플루언서 ID 추출
    username = profile_url.replace("https://www.instagram.com/", "")

    # 저장할 파일명 및 경로
    file_name = f"{base_folder}/{username}{today}.xlsx"

    driver.get(profile_url)
    time.sleep(random.uniform(3, 6))


    # 최신 게시물 5개의 URL 가져오기
    number = 5
    post_links = driver.find_elements(By.CSS_SELECTOR, 'div.xg7h5cd.x1n2onr6 div div a[href*="/"][role="link"]')
    actions = ActionChains(driver)
    post_urls = []
    comment_count = []
    count = 0
    for post in post_links:
        if count >= number:
            break
        try:
        # a 태그(post) 내부에서 svg 태그 찾기
            icon_element = post.find_element(By.CSS_SELECTOR, 'svg')
            icon_label = icon_element.get_attribute("aria-label")  # aria-label 값 가져오기
            if icon_label == "고정 게시물" or icon_label == "Pinned post icon":
                continue
            else:
                print(f"아이콘 라벨: {icon_label}")
                actions.move_to_element(post).perform()
                time.sleep(random.uniform(2, 4))  # 요소가 나타날 시간을 확보
                
                # 댓글 수 가져오기
                try:
                    comment_count_element = post.find_element(By.CSS_SELECTOR, 'ul li:nth-of-type(2) span.html-span.xdj266r')
                    comment_count.append(comment_count_element.text)
                except NoSuchElementException:
                    print("댓글 수를 찾을 수 없습니다. 해당 게시글을 건너뜁니다.")
                    continue  # 댓글 수가 없는 경우, 해당 게시글을 건너뛰고 다음 게시글로 진행

                post_url = post.get_attribute("href")
                post_urls.append(post_url)
                count = count + 1

        except NoSuchElementException:
            print("SVG 아이콘이 존재하지 않음")
            actions.move_to_element(post).perform()
            time.sleep(random.uniform(2, 4))  # 요소가 나타날 시간을 확보
            comment_count_element = post.find_element(By.CSS_SELECTOR, 'ul li:nth-of-type(2) span.html-span.xdj266r')
            comment_count.append(comment_count_element.text)
            post_url = post.get_attribute("href")
            post_urls.append(post_url)
            count = count + 1       

    print(post_urls)
    print(comment_count)


    # data lists
    # 결과를 저장할 리스트
    all_comments_data = []


    number = 1

    # 각 게시물로 이동해 댓글 크롤링
    for post_url in post_urls:
        driver.get(post_url)
        id_f=[]
        rp_f=[]
        rt_f=[]
        time.sleep(random.uniform(5, 10))

        comments_section = WebDriverWait(driver, 10).until(
        EC.visibility_of_element_located((By.CSS_SELECTOR, "ul._a9z6._a9za"))
        )
        
        for i in range(100):
            comment_more_btn = "ul._a9z6._a9za li div button[class='_abl-'][type='button']"
            try:
                more_btn = driver.find_element(By.CSS_SELECTOR, comment_more_btn)
                more_btn.click()
                time.sleep(random.uniform(3, 6))  # 페이지 로딩을 기다리기 위해 잠시 대기
                print("더보기 버튼 클릭")
            except:
                print("더보기 버튼이 더 이상 존재하지 않습니다.")
                break


        #게시글 작성 일자
        date_of_upload = driver.find_element(By.CSS_SELECTOR, "div.x1yztbdb.x1h3rv7z.x1swvt13 div div a span time")

        #게시글 좋아요 수
        like_num = driver.find_element(By.CSS_SELECTOR, "section.x12nagc.x182iqb8.x1pi30zi.x1swvt13 div div span a span span")


        # 아이디와 댓글 내용, 댓글의 작성 날짜 추출
        ids = driver.find_elements(By.CSS_SELECTOR, "ul._a9z6._a9za li h3 a[role='link'][tabindex='0'][href^='/']")
        replies = driver.find_elements(By.CSS_SELECTOR, "ul._a9z6._a9za li span[dir='auto']._aaco")
        time_elements = driver.find_elements(By.CSS_SELECTOR, "ul._a9z6._a9za div._a9zr div span time._a9ze._a9zf")



        # zip으로 아이디와 댓글 매핑
        for id_element, reply_element, time_element in zip(ids, replies, time_elements):
            id_f.append(id_element.text.strip())
            rp_f.append(reply_element.text.strip())
            rt_f.append(time_element.get_attribute("title"))
        
        # 현재 게시물의 index 찾기
        post_index = post_urls.index(post_url)
        

        normalize_like_num =normalize(like_num.text.strip())
        normalize_comment_num = normalize(comment_count[post_index])

        print(f"크롤링된 좋아요 수: {normalize_like_num}")
        print(f"크롤링된 댓글 수: {normalize_comment_num}")

       

        # 📌 게시글 데이터를 전송 (댓글과 별도로)
        
        
        parsed_date = datetime.strptime(date_of_upload.get_attribute("title"), "%Y년 %m월 %d일")
        # 시간 정보를 제거하고 ISO-8601 형식으로 변경
        formatted_date = parsed_date.strftime('%Y-%m-%d')
        post_data = {
            "influencerId": number,
            "url": post_url,
            "postedDate": formatted_date,
            "likeNum": normalize_like_num,
            "commentNum":  normalize_comment_num,
            "createdAt": today
            }   
    
        # Spring Boot에 전송 후 ID 받기
        post_id = send_post_data(post_data)

        # 각 게시물에 대해 추출된 데이터를 리스트에 저장
        for i in range(len(id_f)):
            all_comments_data.append({
                "게시물 URL": post_url, #if i == 0 else "",  # 첫 번째 행만 표시
                "게시물 날짜": date_of_upload.get_attribute("title"), #if i == 0 else "",  # 첫 번째 행만 표시
                "게시물 좋아요 수": like_num.text.strip(), #if i == 0 else "",  # 첫 번째 행만 표시,
                "댓글 수": comment_count[post_index], #if i == 0 else "",  # 첫 번째 행만 표시 (올바른 댓글 수 할당)
                "작성자": id_f[i],
                "댓글": rp_f[i],
                "댓글 작성일": rt_f[i],
                "데이터 수집일": today
            })

        number = number + 1

    # DataFrame 생성
    df = pd.DataFrame(all_comments_data)

    # 엑셀 파일로 저장
    df.to_excel(file_name, index=False)
    print(f"{username}의 크롤링 데이터가 저장되었습니다: {file_name}")

driver.quit()


