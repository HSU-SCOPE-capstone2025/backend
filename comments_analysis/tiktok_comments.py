import os
import pandas as pd
import re
import fasttext

# ───── 모델 경로 설정 ─────
model_path = "/Users/syb/Downloads/lid.176.bin"
model = fasttext.load_model(model_path)

# ───── 언어 코드 → 언어 이름 매핑 ─────
LANGUAGE_LABELS = {
    "en": "영어", "ja": "일본어", "zh": "중국어", "es": "스페인어",
    "fr": "프랑스어", "vi": "베트남어", "de": "독일어", "pt": "포르투갈어",
    "it": "이탈리아어", "id": "인도네시아어", "th": "태국어", "ru": "러시아어",
    "ko": "한국어"
}

# ───── 언어 판별 함수 ─────
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

# ───── 댓글 컬럼 탐색 함수 ─────
def get_comment_column(df):
    for col in ['comment', '내용', 'text', '댓글']:
        if col in df.columns:
            return col
    return None

# ───── 루트 디렉토리 (인플루언서별 댓글 폴더) ─────
root_dir = "/Users/syb/Desktop/comments_analysis/tiktok_influencer_final"
results = []

# ───── 인플루언서 폴더 순회 ─────
for influencer in os.listdir(root_dir):
    influencer_path = os.path.join(root_dir, influencer)
    if not os.path.isdir(influencer_path):
        continue

    comment_file = [f for f in os.listdir(influencer_path) if f.startswith("redata_comments") and f.endswith(".xlsx")]
    if not comment_file:
        continue

    file_path = os.path.join(influencer_path, comment_file[0])
    try:
        df = pd.read_excel(file_path)
        comment_col = get_comment_column(df)
        if not comment_col:
            print(f"❌ 댓글 내용 컬럼 없음: {file_path}")
            continue

        df[["label", "lang"]] = df[comment_col].apply(lambda x: pd.Series(classify_comment_with_lang(x)))
        summary = df["label"].value_counts().to_dict()
        total = len(df)

        # 외국어 언어 분포 상위 3개 추출
        lang_freq = df[df["label"] == "외국인"]["lang"].value_counts(normalize=True).head(3)

        results.append({
            "influencer": influencer,
            "total_comments": total,
            "korean": summary.get("한국인", 0),
            "foreign": summary.get("외국인", 0),
            "other": summary.get("기타", 0),
            "korean_ratio": round(summary.get("한국인", 0) / total * 100, 2) if total else 0,
            "foreign_ratio": round(summary.get("외국인", 0) / total * 100, 2) if total else 0,
            "other_ratio": round(summary.get("기타", 0) / total * 100, 2) if total else 0,
            "top_foreign_langs": ", ".join([
                f"{LANGUAGE_LABELS.get(lang, lang)} ({round(p * 100, 1)}%)"
                for lang, p in lang_freq.items()
            ])
        })

    except Exception as e:
        print(f"⚠️ 오류 발생 - {influencer}: {e}")

# ───── 결과 저장 ─────
if results:
    result_df = pd.DataFrame(results)
    result_df = result_df.sort_values(by="korean_ratio", ascending=False)

    # ⬇️ 저장 경로: root_dir 바깥 폴더
    save_path = "/Users/syb/Desktop/comments_analysis/tiktok_data_language_summary.xlsx"
    result_df.to_excel(save_path, index=False)
    print(f"✅ 저장 완료: {save_path}")
else:
    print("⚠️ 분석 가능한 댓글 데이터가 없습니다.")