package com.fivepointers.selenium;

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

import com.fivepointers.selenium.DriveSelenium.Article;

public class ExpressNews extends News {

	private List<NewsSection> sections = List.of(
			new NewsSection("politics", "https://indianexpress.com/section/political-pulse/"),
			new NewsSection("sports", "https://indianexpress.com/section/sports/"));
	// September 16, 2024 07:38 IST
	private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
			.appendPattern("MMMM dd, yyyy H:m z").toFormatter(Locale.ENGLISH);

	public ExpressNews(String root) {
		super(root, "express");
	}

	@Override
	public void run() {
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		sections.forEach(section -> {
			checkOrCreateFolderAndFiles(section.getTopic());
			driver.get(section.getUrl());
			List<WebElement> elements = driver.findElements(By.className("articles"));
			List<Article> articles = elements.stream().map(element -> getDetails(element))
					.filter(article -> isNewArticle(article.getDate())
							&& isUnreadArticle(article.getUrl(), section.getTopic()))
					.peek(article -> ExpressNews.getFullContent(article))
					.filter(article -> article.getContent() != null)
					.collect(Collectors.toList());
			writeArticles(section.getTopic(), articles);
		});
		driver.quit();
	}

	private Article getDetails(WebElement element) {
		Article article = new Article();
		article.setTitle(element.findElement(By.cssSelector("h2 a")).getText());
		article.setDate(LocalDateTime.parse(element.findElement(By.className("date")).getText(), formatter));
		article.setSynopsys(element.findElement(By.tagName("p")).getText());
		article.setUrl(element.findElement(By.tagName("a")).getAttribute("href"));
		return article;
	}

	private static void getFullContent(Article article) {
		WebDriver driver = new ChromeDriver();
		driver.get(article.getUrl());
		try {
			String fullContent = driver.findElement(By.id("pcl-full-content")).getText().replaceAll("ADVERTISEMENT", "")
					.replace("Click here to join The Indian Express on WhatsApp and get latest news and updates", "");
			article.setContent(fullContent.trim());			
		} catch(org.openqa.selenium.NoSuchElementException ex) {
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
