package com.fivepointers.selenium.service;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fivepointers.selenium.entity.NewsStore;
import com.fivepointers.selenium.model.Article;
import com.fivepointers.selenium.model.NewsSection;
import com.fivepointers.selenium.task.DriveSeleniumTask;

import lombok.Data;

@Data
public abstract class AbstractNewsService {

	private static final Logger log = LoggerFactory.getLogger(AbstractNewsService.class);

	private final NewsStoreService newsStoreService;
	private final String NEWS_CHANNEL_NAME;
	private final int RETRTY_LIMIT = 3;

	public AbstractNewsService(String newsChannelName, NewsStoreService newsStoreService) {
		this.NEWS_CHANNEL_NAME = newsChannelName;
		this.newsStoreService = newsStoreService;
	}

	public abstract void scrapNews(WebDriver driver, WebDriverWait wait, long schedulerId);

	public boolean isUnreadArticle(Article article) {
		return (article.getPublishDate().isAfter(DriveSeleniumTask.lastScraped)
				|| newsStoreService.isUnreadUrl(article.getUrl()))
				&& newsStoreService.isNewOrWithinRetryLimit(article.getUrl(), RETRTY_LIMIT);
	}

	public final List<Article> filterArticles(List<Article> articles, long schedulerId, NewsSection section) {
		List<Article> goodArticles = new ArrayList<>();
		List<Article> errorArticles = new ArrayList<>();
		for (Article article : articles) {
			if (article == null)
				continue;
			if (!article.isError() && !article.getContent().isBlank()) {
				goodArticles.add(article);
			} else {
				errorArticles.add(article);
			}
		}
		newsStoreService.saveErrors(errorArticles, schedulerId, getNEWS_CHANNEL_NAME(), section.getCategory(),
				section.getTag());
		return goodArticles;
	}

	protected void writeArticles(String category, String tag, List<Article> articles) {
		articles.stream().forEach(article -> {
			try {
				NewsStore store = new NewsStore();
				store.setWebsiteName(NEWS_CHANNEL_NAME);
				store.setUrl(article.getUrl());
				store.setNewsTitle(article.getTitle());
				store.setNewsCategory(category);
				store.setNewsWebTag(tag);
				store.setNewsSynopsis(article.getSynopsys());
				store.setOriginalData(article.getContent());
				store.setPublishDate(article.getPublishDate());
				store.setScrapDate(article.getSaveDate());
				newsStoreService.save(store);
			} catch (Exception e) {
				log.error("Not able to save entity with url: " + article.getUrl() + " : " + e.getMessage());
			}
		});
	}

}
