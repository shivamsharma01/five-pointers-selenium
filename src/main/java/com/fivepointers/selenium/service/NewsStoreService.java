package com.fivepointers.selenium.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fivepointers.selenium.entity.NewsStore;
import com.fivepointers.selenium.model.Article;
import com.fivepointers.selenium.repository.NewsStoreRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class NewsStoreService {

	private NewsStoreErrorService newsStoreErrorService;

	private NewsStoreRepository newsStoreRepository;

	public boolean isUnreadUrl(String url) {
		return !newsStoreRepository.existsByUrl(url);
	}

	public void save(NewsStore store) {
		newsStoreRepository.save(store);
	}

	public void saveErrors(List<Article> errorArticles, long schedulerId, String channel, String category, String tag) {
		newsStoreErrorService.saveErrors(errorArticles, schedulerId, channel, category, tag);
	}

	public LocalDateTime findLatestNewsArticleDateTime() {
		LocalDateTime lastScraped = newsStoreRepository.findLatestNewsArticleDateTime();
		lastScraped = lastScraped != null ? lastScraped : LocalDateTime.now().minus(2, ChronoUnit.DAYS);
		return lastScraped;
	}

	public boolean isNewOrWithinRetryLimit(String url, int retryLimit) {
		return newsStoreErrorService.isWithinRetryLimit(url, retryLimit);
	}

	public void deleteByDuration(LocalDateTime tillTime) {
		newsStoreRepository.deleteByDuration(tillTime);
	}
}
