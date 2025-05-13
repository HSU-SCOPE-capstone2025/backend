import os
import pandas as pd
import re
import fasttext

# ───── 모델 경로 설정 ─────
model_path = "/Users/syb/Downloads/lid.176.bin"
model = fasttext.load_model(model_path)

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

# ───── 댓글 컬럼 자동 탐색 ─────
def get_comment_column(df):
    for col in ['comment', '내용', '댓글', 'text']:
        if col in df.columns:
            return col
    return None

# ───── 언어 라벨 매핑 ─────
LANGUAGE_LABELS = {
    "en": "영어", "ja": "일본어", "zh": "중국어", "es": "스페인어",
    "fr": "프랑스어", "vi": "베트남어", "de": "독일어", "pt": "포르투갈어",
    "it": "이탈리아어", "id": "인도네시아어", "th": "태국어", "ru": "러시아어",
}

# ───── 루트 경로 (유튜브 댓글 데이터) ─────
root_dir = "/Users/syb/Desktop/comments_analysis/youtube_data"
results = []

# ───── 인플루언서 폴더 순회 ─────
for influencer in os.listdir(root_dir):
    influencer_path = os.path.join(root_dir, influencer)
    if not os.path.isdir(influencer_path):
        continue

    all_dfs = []
    for file in os.listdir(influencer_path):
        if file.startswith("comments") and file.endswith(".xlsx"):
            try:
                df = pd.read_excel(os.path.join(influencer_path, file))
                comment_col = get_comment_column(df)
                if not comment_col:
                    print(f"❌ 댓글 컬럼 없음: {file}")
                    continue
                all_dfs.append(df[[comment_col]])
            except Exception as e:
                print(f"⚠️ 파일 오류: {file} - {e}")

    if not all_dfs:
        continue

    try:
        merged_df = pd.concat(all_dfs, ignore_index=True)
        comment_col = get_comment_column(merged_df)
        merged_df[["label", "lang"]] = merged_df[comment_col].apply(lambda x: pd.Series(classify_comment_with_lang(x)))

        summary = merged_df["label"].value_counts().to_dict()
        total = len(merged_df)
        top_langs = merged_df[merged_df["label"] == "외국인"]["lang"].value_counts(normalize=True).head(3).to_dict()

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
                f"{LANGUAGE_LABELS.get(lang, lang)} ({round(p*100, 1)}%)"
                for lang, p in top_langs.items()
            ])
        })

    except Exception as e:
        print(f"⚠️ 분석 오류 - {influencer}: {e}")

# ───── 결과 저장 ─────
if results:
    result_df = pd.DataFrame(results)
    result_df = result_df.sort_values(by="korean_ratio", ascending=False)
    output_path = "/Users/syb/Desktop/comments_analysis/youtube_data_language_summary.xlsx"
    result_df.to_excel(output_path, index=False)
    print(f"✅ 저장 완료: {output_path}")
else:
    print("⚠️ 분석할 유효한 댓글 데이터가 없습니다.")


# /opt/anaconda3/bin/python /Users/syb/Desktop/comments_analysis/youtube_comments.py