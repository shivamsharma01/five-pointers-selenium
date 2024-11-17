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
import org.openqa.selenium.support.ui.ExpectedConditions;
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
public class JagranNewsService extends AbstractNewsService {

	private static final Logger log = LoggerFactory.getLogger(JagranNewsService.class);

	// SUN, 29 SEP 2024 01:34 PM (IST)
	private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
			.appendPattern("EEE, dd MMM yyyy hh:mm a (z)").toFormatter(Locale.ENGLISH);

	private final NewsStoreService newsStoreService;

	private final List<NewsSection> sections = List.of(
			new NewsSection("National", "National", "https://www.jagran.com/news/national-news-hindi.html"),
			new NewsSection("International", "International-USA",
					"https://www.jagran.com/world/america-news-hindi.html"),
			new NewsSection("International", "International-PAKISTAN",
					"https://www.jagran.com/world/pakistan-news-hindi.html"),
			new NewsSection("International", "International-CHINA",
					"https://www.jagran.com/world/china-news-hindi.html"),
			new NewsSection("International", "International-MIDDLE-EAST",
					"https://www.jagran.com/world/middle-east-news-hindi.html"),
			new NewsSection("Entertainment", "Entertainment-Bollywood",
					"https://www.jagran.com/entertainment/bollywood-news-hindi.html"),
			new NewsSection("Entertainment", "Entertainment-Web-Series",
					"https://www.jagran.com/entertainment/web-series-review-news-hindi.html"),
			new NewsSection("Cricket", "Cricket-Bouncer", "https://www.jagran.com/cricket/bouncer-news-hindi.html"),
			new NewsSection("Cricket", "Cricket-Headlines", "https://www.jagran.com/cricket/headlines-news-hindi.html"),
			new NewsSection("Business", "Business", "https://www.jagran.com/business/biz-news-hindi.html")

	);

	public JagranNewsService(NewsStoreService newsStoreService) {
		super("Jagran", newsStoreService);
		this.newsStoreService = newsStoreService;
	}

	public void scrapNews(WebDriver driver, WebDriverWait wait, long schedulerId) {
		sections.forEach(section -> {
			driver.get(section.getUrl());
			List<WebElement> elements = wait
					.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".stickySidebar ul li")));
			List<String> articleUrls = elements.stream().map(element -> getUrl(element))
					.collect(Collectors.toList());

			List<Article> articles = articleUrls.stream().filter(url -> url != null && !url.isBlank())
					.map(url -> getFullContent(url, driver)).toList();

			articles = filterArticles(articles, schedulerId, section).stream()
					.filter(article -> isUnreadArticle(article)).toList();

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

	private static Article getFullContent(String url, WebDriver driver) {
		Article article = new Article();
		article.setSaveDate(LocalDateTime.now());
		article.setUrl(url);
		driver.get(url);
		try {
			WebElement element = driver.findElement(By.className("main-col"));
			wayOne(element, article);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			article.setContent(ex.getMessage());
			article.setError(true);
			log.error("Not able to fetch news article with url " + article.getUrl());
		} finally {
			try {
				Thread.sleep(2000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return article;
	}

	private static void wayOne(WebElement element, Article article) {
		String textString;
		try {
			textString = element.findElement(By.className("ArticleBody")).getText();
			article.setContent(textString);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			article.setContent(ex.getMessage());
			article.setError(true);
			log.error("Not able to fetch news article details: " + article.getUrl());
			return;
		}
		try {
			textString = element.findElement(By.tagName("h1")).getText();
			article.setTitle(textString);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			article.setTitle("Failed to load Title");
			log.error("Not able to fetch news article title: " + article.getUrl());
		}
		try {
			textString = element.findElement(By.cssSelector("span[class*='_date_']")).getText();
			if (textString.contains(":")) {
				article.setPublishDate(LocalDateTime.parse(textString.split(":", 2)[1].trim(), formatter));
			} else {
				article.setPublishDate(LocalDateTime.now());
				log.error("Not able to fetch news article date: " + article.getUrl());
			}
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			article.setPublishDate(LocalDateTime.now());
			log.error("Not able to fetch news article date: " + article.getUrl());
		}
		try {
			textString = element.findElement(By.cssSelector("p[class*='_shortdescription_']")).getText();
			article.setSynopsis(textString);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			article.setSynopsis("Failed to load Title");
			log.error("Not able to fetch news article synopsis: " + article.getUrl());
		}
		article.setError(false);
	}

}
