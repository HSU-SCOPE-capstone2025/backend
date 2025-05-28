import os
import shutil

def delete_chrome_cache(user_data_dir_name="chrome_user_data"):
    """
    크롬 드라이버 캐시(세션, 쿠키 등)가 저장된 폴더를 삭제하는 함수.

    Args:
        user_data_dir_name (str): 삭제할 사용자 데이터 폴더 이름 (기본값: chrome_user_data)
    """
    user_data_path = os.path.join(os.getcwd(), user_data_dir_name)
    
    if os.path.exists(user_data_path):
        try:
            shutil.rmtree(user_data_path)
            print(f"[성공] 크롬 캐시 폴더 '{user_data_dir_name}' 삭제 완료.")
        except Exception as e:
            print(f"[오류] 폴더 삭제 실패: {e}")
    else:
        print(f"[안내] '{user_data_dir_name}' 폴더가 존재하지 않습니다. 이미 삭제된 상태일 수 있습니다.")

if __name__ == "__main__":
    delete_chrome_cache()