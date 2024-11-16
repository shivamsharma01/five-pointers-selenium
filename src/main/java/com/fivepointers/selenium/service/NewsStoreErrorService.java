package com.fivepointers.selenium.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fivepointers.selenium.entity.NewsStoreError;
import com.fivepointers.selenium.model.Article;
import com.fivepointers.selenium.repository.NewsStoreErrorRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class NewsStoreErrorService {

	private NewsStoreErrorRepository newsStoreErrorRepository;

	public void saveErrors(List<Article> errorArticles, long schedulerId, String channel, String category, String tag) {
		List<NewsStoreError> errors = errorArticles.stream().map(article -> {
			NewsStoreError err = newsStoreErrorRepository.findByUrl(article.getUrl()).or(new NewsStoreError());
			err.setWebsiteName(channel);
			err.setNewsCategory(category);
			err.setNewsWebTag(tag);
			err.setErrorDateTime(article.getSaveDate());
			err.setUrl(article.getUrl());
			err.setErrorMsg(article.getContent());
			err.setSchedulerId(schedulerId);
			err.setNumRetry(err.getNumRetry() + 1);
			return err;
		}).collect(Collectors.toList());
		newsStoreErrorRepository.saveAll(errors);
	}

	public boolean isWithinRetryLimit(String url, int retryLimit) {
		return newsStoreErrorRepository.isWithinRetryLimit(url, retryLimit);
	}
}
