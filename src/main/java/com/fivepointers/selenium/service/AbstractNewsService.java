package com.fivepointers.selenium.service;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fivepointers.selenium.entity.NewsStore;
import com.fivepointers.selenium.model.Article;
import com.fivepointers.selenium.repository.NewsStoreRepository;

import lombok.Data;

@Data
public abstract class AbstractNewsService {

	private static final Logger log = LoggerFactory.getLogger(AbstractNewsService.class);

	private final NewsStoreRepository newsStoreRepository;
	private final String NEWS_CHANNEL_NAME;

	public AbstractNewsService(String newsChannelName, NewsStoreRepository newsStoreRepository) {
		this.NEWS_CHANNEL_NAME = newsChannelName;
		this.newsStoreRepository = newsStoreRepository;
	}

	public abstract void scrapNews();

	public boolean isUnreadArticle(String url) {
		return newsStoreRepository.existsByUrl(url);
	}

	protected void writeArticles(String topic, List<Article> articles) {
		articles.stream().forEach(article -> {
			try {
				NewsStore store = new NewsStore();
				store.setWebsiteName(NEWS_CHANNEL_NAME);
				store.setUrl(article.getUrl());
				store.setNewsTitle(article.getTitle());
				store.setNewsSynopsis(article.getSynopsys());
				store.setOriginalData(article.getContent());
				store.setPublishDate(article.getPublishDate());
				store.setScrapDate(article.getSaveDate());
				newsStoreRepository.save(store);
			} catch (Exception e) {
				log.error("Not able to save entity with url: " + article.getUrl() + " : " + e.getMessage());
			}
		});
	}

}
