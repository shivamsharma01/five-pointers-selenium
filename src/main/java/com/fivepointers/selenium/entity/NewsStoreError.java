package com.fivepointers.selenium.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "news_store_error")
@Entity
public class NewsStoreError {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(nullable = false)
	private String url;
	@Column(name = "error_msg", nullable = false)
	private String errorMsg;
	@Column(name = "website_name", nullable = false)
	private String websiteName;
	@Column(name = "news_category", nullable = false)
	private String newsCategory;
	@Column(name = "news_website_tag")
	private String newsWebTag;
	@Column(name = "num_retry", nullable = false)
	private int numRetry;
	@Column(name = "scheduler_id", nullable = false)
	private long schedulerId;
	@Column(name = "error_date", nullable = false)
	private LocalDateTime errorDateTime;
}