CREATE TABLE news_store (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    website_name VARCHAR(64) NOT NULL,
    url VARCHAR(512) NOT NULL,
    news_title TEXT NOT NULL,
    news_synopsys TEXT,
    news_data_original TEXT NOT NULL,
    news_data_modified TEXT,
    publish_date TIMESTAMP,
    scrap_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE news_store_errors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(512) NOT NULL,
    error_msg TEXT NOT NULL,
    scheduler_id BIGINT NOT NULL,
    error_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE scheduler_entry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scrap_start_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	duration INTEGER NOT NULL,
	is_processing_success BOOLEAN NOT NULL
);