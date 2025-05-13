import os
import pandas as pd
import re
import fasttext
from collections import defaultdict

# ───── fastText 모델 로드 ─────
model_path = "/Users/syb/Downloads/lid.176.bin"
model = fasttext.load_model(model_path)

# ───── 언어 코드 → 언어명 매핑 ─────
LANGUAGE_LABELS = {
    "en": "영어", "ja": "일본어", "zh": "중국어", "es": "스페인어", "fr": "프랑스어",
    "vi": "베트남어", "de": "독일어", "pt": "포르투갈어", "it": "이탈리아어",
    "id": "인도네시아어", "th": "태국어", "ru": "러시아어", "ca": "카탈루냐어",
    "sv": "스웨덴어", "no": "노르웨이어", "ar": "아랍어", "ko": "한국어"
}

def classify_comment_with_lang(text):
    text = str(text).strip()
    if not text or re.sub(r'[^\w\s가-힣]', '', text) == '':
        return "기타", None
    try:
        prediction = model.predict(text)[0][0]
        lang_code = prediction.replace("__label__", "")
        if lang_code == "ko":
            return "한국인", "ko"
        else:
            return "외국인", lang_code
    except:
        return "기타", None

def get_comment_column(df):
    for col in ['comment', '내용', '댓글']:
        if col in df.columns:
            return col
    return None

# ───── 분석 대상 폴더 ─────
base_dir = "/Users/syb/Desktop/comments_analysis/instagram_influencer"
comment_map = defaultdict(list)

# ───── 날짜 폴더 순회 ─────
for folder in os.listdir(base_dir):
    folder_path = os.path.join(base_dir, folder)
    if not os.path.isdir(folder_path) or not re.match(r"2025-03-\d{2}", folder):
        continue

    for file in os.listdir(folder_path):
        if not file.endswith(".xlsx"):
            continue

        file_path = os.path.join(folder_path, file)

        # ✅ 인플루언서 이름만 추출
        raw_name = file.replace(".xlsx", "")
        influencer = re.sub(r"[_\-]?\d{4}[-_]\d{2}[-_]\d{2}", "", raw_name)
        influencer = influencer.strip("_-").lower()

        try:
            df = pd.read_excel(file_path)
            comment_col = get_comment_column(df)
            if not comment_col:
                continue

            comment_map[influencer].extend(df[comment_col].dropna().tolist())

        except Exception as e:
            print(f"⚠️ 오류 - {file}: {e}")

# ───── 분석 및 결과 저장 ─────
results = []

for influencer, comments in comment_map.items():
    labeled = [classify_comment_with_lang(c) for c in comments]
    labels = [label for label, _ in labeled]
    langs = [lang for label, lang in labeled if label == "외국인"]

    total = len(labels)
    korean = labels.count("한국인")
    foreign = labels.count("외국인")
    other = labels.count("기타")

    lang_freq = pd.Series(langs).value_counts(normalize=True).head(3)

    results.append({
        "influencer": influencer,
        "korean_ratio": round(korean / total * 100, 2) if total else 0,
        "foreign_ratio": round(foreign / total * 100, 2) if total else 0,
        "other_ratio": round(other / total * 100, 2) if total else 0,
        "top_foreign_langs": ", ".join([
            f"{LANGUAGE_LABELS.get(lang, lang)} ({round(p * 100, 1)}%)"
            for lang, p in lang_freq.items()
        ])
    })

# 정렬 및 저장
df_result = pd.DataFrame(results)
df_result = df_result.sort_values(by="korean_ratio", ascending=False)
save_path = "/Users/syb/Desktop/comments_analysis/instagram_data_language_summary.xlsx"
df_result.to_excel(save_path, index=False)
print(f"✅ 최종 결과 저장 완료: {save_path}")