package com.fivepointers.selenium.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fivepointers.selenium.model.Article;
import com.fivepointers.selenium.model.NewsSection;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Service
public class NavbharatNewsService extends AbstractNewsService {

	private static final Logger log = LoggerFactory.getLogger(NavbharatNewsService.class);

	// 30 Sept 2024, 6:19 pm
	private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
			.appendPattern("dd MMM yyyy, h:mm a").toFormatter(Locale.ENGLISH);

	private final NewsStoreService newsStoreService;
	private final List<NewsSection> sections = List.of(
			new NewsSection("National", "National",
					"https://navbharattimes.indiatimes.com/india/articlelist/1564454.cms"),
			new NewsSection("International", "International",
					"https://navbharattimes.indiatimes.com/world/articlelist/2279801.cms"));

	public NavbharatNewsService(NewsStoreService newsStoreService) {
		super("Navbharat", newsStoreService);
		this.newsStoreService = newsStoreService;
	}

	public void scrapNews(WebDriver driver, WebDriverWait wait, long schedulerId) {
		sections.forEach(section -> {
			driver.get(section.getUrl());
			List<WebElement> elements = driver.findElements(By.cssSelector("ul.medium_listing li"));
			List<String> articlesUrl = elements.stream().map(element -> getUrl(element))
					.filter(url -> url != null && !url.isBlank()).toList();

			List<Article> articles = articlesUrl.stream().map(url -> getFullContent(url, driver))
					.collect(Collectors.toList());

			articles = filterArticles(articles, schedulerId, section).stream()
					.filter(article -> isUnreadArticle(article)).collect(Collectors.toList());
			writeArticles(section.getCategory(), section.getTag(), articles);
		});
	}

	private String getUrl(WebElement element) {
		String url;
		try {
			url = element.findElement(By.tagName("a")).getAttribute("href");
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			log.error("Not able to fetch news url ");
			url = null;
		}
		return url;
	}

	private Article getFullContent(String url, WebDriver driver) {
		Article article = new Article();
		article.setUrl(url);
		driver.get(url);
		try {
			WebElement element = driver.findElement(By.className("story-article"));
			wayOne(element, article);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			article.setContent(ex.getMessage());
			article.setError(true);
			log.error("Not able to fetch news article with url " + article.getUrl());
		}
		try {
			Thread.sleep(2000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		article.setError(false);
		return article;
	}

	private static void wayOne(WebElement element, Article article) {
		String textString;
		try {
			textString = element.findElement(By.className("full_article")).getText();
			article.setContent(textString);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			log.error("Not able to fetch news article details: " + article.getUrl());
		}
		try {
			textString = element.findElement(By.tagName("h1")).getText();
			article.setTitle(textString);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			log.error("Not able to fetch news article title: " + article.getUrl());
		}
		try {
			textString = element.findElement(By.cssSelector("span.time")).getText();
			article.setPublishDate(LocalDateTime.parse(textString.trim(), formatter));
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			article.setPublishDate(LocalDateTime.now());
			log.error("Not able to fetch news article date: " + article.getUrl());
		}
		try {
			textString = element.findElement(By.cssSelector("h2")).getText();
			article.setSynopsys(textString);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			log.error("Not able to fetch news article synopsis: " + article.getUrl());
		}
	}

}
