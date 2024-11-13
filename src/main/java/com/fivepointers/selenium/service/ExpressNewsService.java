package com.fivepointers.selenium.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fivepointers.selenium.DriveSeleniumTask;
import com.fivepointers.selenium.NewsSection;
import com.fivepointers.selenium.model.Article;
import com.fivepointers.selenium.repository.NewsStoreRepository;

@Service
public class ExpressNewsService extends AbstractNewsService {

	private static final Logger log = LoggerFactory.getLogger(ExpressNewsService.class);

	private List<NewsSection> sections = List.of(
			new NewsSection("politics", "https://indianexpress.com/section/political-pulse/"),
			new NewsSection("sports", "https://indianexpress.com/section/sports/"));

	public ExpressNewsService(String newsChannelName, NewsStoreRepository newsStoreRepository) {
		super(newsChannelName, newsStoreRepository);
	}

	public void scrapNews() {
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		sections.forEach(section -> {
			driver.get(section.getUrl());
			List<WebElement> elements = driver.findElements(By.className("articles"));
			List<Article> articles = elements.stream().map(element -> getDetails(element))
					.filter(article -> article.getPublishDate().isAfter(DriveSeleniumTask.lastScraped)
							|| isUnreadArticle(article.getUrl()))
					.peek(article -> getFullContent(article)).filter(article -> article.getContent() != null)
					.collect(Collectors.toList());
			writeArticles(section.getTopic(), articles);
		});
		driver.quit();
	}

	private Article getDetails(WebElement element) {
		Article article = new Article();
		article.setSaveDate(LocalDateTime.now());
		try {
			article.setTitle(element.findElement(By.cssSelector("h2 a")).getText());
		} catch (Exception ex) {
			log.error("getDetails: failed to load title " + element);
		}
		try {
			article.setPublishDate(
					LocalDateTime.parse(element.findElement(By.className("date")).getText(), this.getFormatter()));
		} catch (Exception ex) {
			log.error("getDetails: failed to load date " + element);
		}
		try {
			article.setSynopsys(element.findElement(By.tagName("p")).getText());
		} catch (Exception ex) {
			log.error("getDetails: failed to load synopsis " + element);
		}
		try {
			article.setUrl(element.findElement(By.tagName("a")).getAttribute("href"));
		} catch (Exception ex) {
			log.error("getDetails: failed to load url " + element);
		}
		return article;
	}

	private static void getFullContent(Article article) {
		WebDriver driver = new ChromeDriver();
		driver.get(article.getUrl());
		try {
			String fullContent = driver.findElement(By.id("pcl-full-content")).getText().replaceAll("ADVERTISEMENT", "")
					.replace("Click here to join The Indian Express on WhatsApp and get latest news and updates", "");
			article.setContent(fullContent.trim());
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			log.error("getFullContent: failed to load content " + article.getUrl());
			article.setContent(null);
		}
		driver.close();
		try {
			Thread.sleep(2000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
