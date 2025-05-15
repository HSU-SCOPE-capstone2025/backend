
# 📄 README: SNS 댓글 데이터 MySQL 업로드 가이드

이 문서는 **YouTube, Instagram, TikTok 댓글 데이터를 MySQL에 업로드**하는 과정을 정리한 문서입니다.  
각 플랫폼별 테이블 생성 SQL과 Python 업로드 스크립트 실행 순서를 포함합니다.

---

## ✅ 1. Instagram 댓글 업로드

### 1-1. 테이블 구조 설정

```sql
ALTER TABLE instagram MODIFY post_url VARCHAR(500);

ALTER TABLE instagram ADD UNIQUE (post_url);

CREATE TABLE instagram_comment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_url VARCHAR(500),
    comment TEXT,
    comment_date DATE,
    emotion TEXT,
    topic TEXT,
    cluster TEXT,
    score INT,
    fss FLOAT,
    FOREIGN KEY (post_url) REFERENCES instagram(post_url)
);
```

### 1-2. Python 스크립트 실행

```bash
python instagram_comments.py
```

---

## ✅ 2. YouTube 댓글 업로드

### 2-1. 테이블 구조 설정

```sql
ALTER TABLE youtube MODIFY video_url VARCHAR(191)
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci
NOT NULL;

ALTER TABLE youtube DROP INDEX idx_video_url;
ALTER TABLE youtube ADD UNIQUE idx_video_url (video_url);

CREATE TABLE youtube_comment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    video_url VARCHAR(191)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci,
    comment TEXT,
    comment_date DATE,
    emotion TEXT,
    topic TEXT,
    cluster TEXT,
    score INT,
    fss FLOAT,
    FOREIGN KEY (video_url) REFERENCES youtube(video_url)
);
```

### 2-2. Python 스크립트 실행

```bash
python youtube_comments.py
```

---

## ✅ 3. TikTok 댓글 업로드

### 3-1. 테이블 구조 설정

```sql
ALTER TABLE tiktok
MODIFY video_url VARCHAR(191)
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci
NOT NULL;

ALTER TABLE tiktok
ADD UNIQUE idx_video_url (video_url);

CREATE TABLE tiktok_comment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    video_url VARCHAR(191)
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci,
    comment TEXT,
    comment_date DATE,
    emotion TEXT,
    topic TEXT,
    cluster TEXT,
    score INT,
    fss FLOAT,
    FOREIGN KEY (video_url) REFERENCES tiktok(video_url)
);
```

### 3-2. Python 스크립트 실행

```bash
python tiktok_commnets.py
```

---

## 📌 참고 사항

- 모든 `*_url` 필드는 외래 키 연결을 위해 반드시 **UNIQUE 인덱스**가 필요합니다.
- 데이터가 많을 경우 `to_sql(..., chunksize=500)` 등의 옵션을 주어 부분 업로드를 권장합니다.
- `utf8mb4` 및 `utf8mb4_unicode_ci` 설정은 **이모지, 다국어 처리**를 위한 필수 설정입니다.
- 스크립트 실행 전 반드시 **MySQL 서버가 실행 중인지 확인**하고, 해당 데이터베이스(`test0514`)가 생성되어 있어야 합니다.
