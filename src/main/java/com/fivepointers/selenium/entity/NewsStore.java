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
@Table(name = "news_store")
@Entity
public class NewsStore {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "website_name", nullable = false)
	private String websiteName;
	@Column(nullable = false)
	private String url;
	@Column(name = "news_title", nullable = false)
	private String newsTitle;
	@Column(name = "news_category", nullable = false)
	private String newsCategory;
	@Column(name = "news_website_tag")
	private String newsWebTag;
	@Column(name = "news_synopsis")
	private String newsSynopsis;
	@Column(name = "news_data_original", nullable = false)
	private String originalData;
	@Column(name = "news_data_modified")
	private String modifiedData;
	@Column(name = "publish_date")
	private LocalDateTime publishDate;
	@Column(name = "scrap_date", nullable = false)
	private LocalDateTime scrapDate;
	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;
	@Column(name = "updated_by")
	private long updatedBy;
}
