CREATE DATABASE IF NOT EXISTS scope
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

CREATE TABLE influencer (
  influencer_num INT PRIMARY KEY,
  influencer_name VARCHAR(255),
  categories VARCHAR(255),
  tags VARCHAR(255)
);

CREATE TABLE youtube_video (
  video_url VARCHAR(500) PRIMARY KEY,
  upload_date DATE,
  date DATE,
  view_count INT,
  like_count INT,
  comment_count INT,
  subscriber_count INT,
  channel_url VARCHAR(500),
  channel_title VARCHAR(255),
  channel_description TEXT,
  topic_categories VARCHAR(255),
  title VARCHAR(255),
  description TEXT,
  tags VARCHAR(500),
  thumbnails TEXT,
  influencer_name VARCHAR(255),
  influencer_num INT,
  FOREIGN KEY (influencer_num) REFERENCES influencer(influencer_num)
);

CREATE TABLE tiktok_video (
  video_url VARCHAR(500) PRIMARY KEY,
  description TEXT,
  like_count INT,
  comment_count INT,
  share_count INT,
  upload_date DATE,
  view_count INT,
  follower_num INT,
  user_id VARCHAR(255),
  user_name VARCHAR(255),
  influencer_name VARCHAR(255),
  influencer_num INT,
  influencer_url VARCHAR(500),
  FOREIGN KEY (influencer_num) REFERENCES influencer(influencer_num)
);

CREATE TABLE instagram_post (
  post_url VARCHAR(500) PRIMARY KEY,
  post_date DATE,
  like_count INT,
  comment_count INT,
  at_time DATETIME,
  follower_num INT,
  user_id VARCHAR(255),
  user_name VARCHAR(255),
  influencer_url VARCHAR(500),
  influencer_name VARCHAR(255),
  influencer_num INT,
  FOREIGN KEY (influencer_num) REFERENCES influencer(influencer_num)
);

CREATE TABLE ad_price_estimation (
  influencer_num INT,
  ad_price_range_insta VARCHAR(255),
  ad_price_range_tiktok VARCHAR(255),
  ad_price_range_youtube VARCHAR(255),
  FOREIGN KEY (influencer_num) REFERENCES influencer(influencer_num)
);

CREATE TABLE total_followers (
  influencer_num INT,
  influencer_name VARCHAR(255),
  platform VARCHAR(50),
  date DATE,
  subscriber_count INT,
  FOREIGN KEY (influencer_num) REFERENCES influencer(influencer_num)
);

CREATE TABLE youtube_language_summary (
  influencer_num INT,
  influencer_name VARCHAR(255),
  language_code VARCHAR(50),
  language_name VARCHAR(255),
  percentage FLOAT,
  FOREIGN KEY (influencer_num) REFERENCES influencer(influencer_num)
);

CREATE TABLE tiktok_language_summary (
  influencer_num INT,
  influencer_name VARCHAR(255),
  language_code VARCHAR(50),
  language_name VARCHAR(255),
  percentage FLOAT,
  FOREIGN KEY (influencer_num) REFERENCES influencer(influencer_num)
);

CREATE TABLE instagram_language_summary (
  influencer_num INT,
  influencer_name VARCHAR(255),
  language_code VARCHAR(50),
  language_name VARCHAR(255),
  percentage FLOAT,
  FOREIGN KEY (influencer_num) REFERENCES influencer(influencer_num)
);