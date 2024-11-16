CREATE TABLE news_store (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    website_name VARCHAR(64) NOT NULL,
    url VARCHAR(512) NOT NULL,
    news_title TEXT NOT NULL,
    news_category TEXT NOT NULL,
    news_website_tag TEXT,
    news_synopsis TEXT,
    news_data_original TEXT NOT NULL,
    news_data_modified TEXT,
    publish_date TIMESTAMP,
    scrap_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT
);

CREATE TABLE news_store_error (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    website_name VARCHAR(64) NOT NULL,
    news_category TEXT NOT NULL,
    num_retry INT NOT NULL DEFAULT 1,
    news_website_tag TEXT,
    url VARCHAR(512) NOT NULL,
    error_msg TEXT NOT NULL,
    scheduler_id BIGINT NOT NULL,
    error_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE scheduler_entry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scrap_start_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	duration INTEGER NOT NULL DEFAULT -1,
	is_processing_success BOOLEAN NOT NULL DEFAULT FALSE
);