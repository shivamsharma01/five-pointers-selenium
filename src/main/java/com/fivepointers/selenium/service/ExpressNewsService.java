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
public class ExpressNewsService extends AbstractNewsService {

	private static final Logger log = LoggerFactory.getLogger(ExpressNewsService.class);
	// September 16, 2024 07:38 IST
	private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
			.appendPattern("MMMM dd, yyyy H:m z").toFormatter(Locale.ENGLISH);

	private final NewsStoreService newsStoreService;

	private final List<NewsSection> sections = List.of(
			new NewsSection("Politics", "politics", "https://indianexpress.com/section/political-pulse/"),
			new NewsSection("Sports", "sports", "https://indianexpress.com/section/sports/"));

	public ExpressNewsService(NewsStoreService newsStoreService) {
		super("Express News", newsStoreService);
		this.newsStoreService = newsStoreService;
	}

	public void scrapNews(WebDriver driver, WebDriverWait wait, long schedulerId) {
		sections.forEach(section -> {
			driver.get(section.getUrl());
			List<WebElement> elements = wait
					.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("articles")));

			List<Article> articles = elements.stream().map(element -> getDetails(element))
					.filter(article -> article != null && isUnreadArticle(article)).collect(Collectors.toList());

			articles = articles.stream().peek(article -> getFullContent(article, driver)).toList();

			articles = filterArticles(articles, schedulerId, section).stream().toList();

			writeArticles(section.getCategory(), section.getTag(), articles);
		});
	}

	private Article getDetails(WebElement element) {
		Article article = new Article();
		try {
			article.setUrl(element.findElement(By.tagName("a")).getAttribute("href"));
		} catch (Exception ex) {
			log.error("getDetails: failed to load url " + element);
			return null;
		}
		article.setSaveDate(LocalDateTime.now());
		try {
			article.setTitle(element.findElement(By.cssSelector("h2 a")).getText());
		} catch (Exception ex) {
			article.setTitle("Failed to load Title");
			log.error("getDetails: failed to load title " + element);
		}
		try {
			article.setPublishDate(LocalDateTime.parse(element.findElement(By.className("date")).getText(), formatter));
		} catch (Exception ex) {
			article.setPublishDate(LocalDateTime.now());
			log.error("getDetails: failed to load date " + element);
		}
		try {
			article.setSynopsis(element.findElement(By.tagName("p")).getText());
		} catch (Exception ex) {
			article.setSynopsis("Failed to load Synopsys");
			log.error("getDetails: failed to load synopsis " + element);
		}
		return article;
	}

	private static void getFullContent(Article article, WebDriver driver) {
		driver.get(article.getUrl());
		try {
			String fullContent = driver.findElement(By.id("pcl-full-content")).getText().replaceAll("ADVERTISEMENT", "")
					.replace("Click here to join The Indian Express on WhatsApp and get latest news and updates", "");
			article.setContent(fullContent.trim());
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			article.setContent(ex.getMessage());
			article.setError(true);
			log.error("getFullContent: failed to load content " + article.getUrl());
		}
		try {
			Thread.sleep(2000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		article.setError(false);
	}
}
