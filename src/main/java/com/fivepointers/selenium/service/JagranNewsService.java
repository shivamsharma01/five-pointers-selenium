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
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fivepointers.selenium.model.Article;
import com.fivepointers.selenium.model.NewsSection;
import com.fivepointers.selenium.repository.NewsStoreRepository;
import com.fivepointers.selenium.task.DriveSeleniumTask;

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

	private List<NewsSection> sections = List.of(
			new NewsSection("National", "https://www.jagran.com/news/national-news-hindi.html"),
			new NewsSection("International-USA", "https://www.jagran.com/world/america-news-hindi.html"),
			new NewsSection("International-PAKISTAN", "https://www.jagran.com/world/pakistan-news-hindi.html"),
			new NewsSection("International-CHINA", "https://www.jagran.com/world/china-news-hindi.html"),
			new NewsSection("International-MIDDLE-EAST", "https://www.jagran.com/world/middle-east-news-hindi.html"),
			new NewsSection("Entertainment-Bollywood",
					"https://www.jagran.com/entertainment/bollywood-news-hindi.html"),
			new NewsSection("Entertainment-Web-Series",
					"https://www.jagran.com/entertainment/web-series-review-news-hindi.html"),
			new NewsSection("Cricket-Bouncer", "https://www.jagran.com/cricket/bouncer-news-hindi.html"),
			new NewsSection("Cricket-Headlines", "https://www.jagran.com/cricket/headlines-news-hindi.html"),
			new NewsSection("Business", "https://www.jagran.com/business/biz-news-hindi.html")

	);

	public JagranNewsService(String newsChannelName, NewsStoreRepository newsStoreRepository) {
		super(newsChannelName, newsStoreRepository);
	}

	public void scrapNews() {
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		sections.forEach(section -> {
			driver.get(section.getUrl());
			List<WebElement> elements = driver.findElements(By.cssSelector(".stickySidebar ul li"));
			List<Article> articles = elements.stream().map(element -> getUrl(element))
					.filter(url -> url != null && !url.isBlank()).map(url -> getFullContent(url))
					.filter(article -> article != null && !article.getContent().isBlank())
					.filter(article -> article.getPublishDate().isAfter(DriveSeleniumTask.lastScraped)
							|| isUnreadArticle(article.getUrl()))
					.collect(Collectors.toList());
			writeArticles(section.getTopic(), articles);
		});
		driver.quit();
	}

	private String getUrl(WebElement element) {
		String url;
		try {
			url = element.findElement(By.tagName("a")).getAttribute("href");
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			url = null;
		}
		return url;
	}

	private static Article getFullContent(String url) {
		WebDriver driver = new ChromeDriver();
		Article article = new Article();
		article.setSaveDate(LocalDateTime.now());
		article.setUrl(url);
		driver.get(url);
		try {
			WebElement element = driver.findElement(By.className("main-col"));
			wayOne(element, article);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			log.error("Not able to fetch news article with url " + article.getUrl());
			return null;
		}
		driver.close();
		try {
			Thread.sleep(2000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return article;
	}

	private static void wayOne(WebElement element, Article article) {
		String textString;
		try {
			textString = element.findElement(By.className("ArticleBody")).getText();
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
			textString = element.findElement(By.cssSelector("span[class*='_date_']")).getText();
			if (textString.contains(":")) {
				article.setPublishDate(LocalDateTime.parse(textString.split(":", 2)[1].trim(), formatter));
			} else {
				log.error("Not able to fetch news article date: " + article.getUrl());
			}
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			log.error("Not able to fetch news article date: " + article.getUrl());
		}
		try {
			textString = element.findElement(By.cssSelector("p[class*='_shortdescription_']")).getText();
			article.setSynopsys(textString);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			log.error("Not able to fetch news article synopsis: " + article.getUrl());
		}
	}

}
