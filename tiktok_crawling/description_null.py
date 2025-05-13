import os
import pandas as pd

def check_remaining_work(base_folder):
    incomplete_list = []

    for influencer in os.listdir(base_folder):
        stats_path = os.path.join(base_folder, influencer, f"redata_stats_{influencer}.xlsx")
        if not os.path.exists(stats_path):
            continue

        df = pd.read_excel(stats_path)

        if "description" not in df.columns:
            print(f"❗ {influencer}: description 컬럼 없음 (완료되지 않음)")
            incomplete_list.append(influencer)
            continue

        missing = df["description"].isna() | (df["description"].astype(str).str.strip() == "")
        num_missing = missing.sum()

        if num_missing > 0:
            print(f"🔁 {influencer}: {num_missing}개 description 비어 있음")
            incomplete_list.append(influencer)
        else:
            print(f"✅ {influencer}: 완료됨")

    print("\n🟡 수집 미완료 인플루언서:", incomplete_list)
    return incomplete_list

# 사용 예시
FINAL_FOLDER = "/Users/syb/Desktop/data_merge/tiktok/tiktok_influencer_final"
check_remaining_work(FINAL_FOLDER)