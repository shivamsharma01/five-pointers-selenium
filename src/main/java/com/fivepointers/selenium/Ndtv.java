package com.fivepointers.selenium;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.fivepointers.selenium.DriveSelenium.Article;

public class Ndtv extends News {

	private List<NewsSection> sections = List.of(
//			new NewsSection("National", "https://ndtv.in/india")
			new NewsSection("International", "https://ndtv.in/world-news")
			
//			new NewsSection("International", "https://navbharattimes.indiatimes.com/world/articlelist/2279801.cms")
//			new NewsSection("International-PAKISTAN", "https://www.jagran.com/world/pakistan-news-hindi.html"),
//			new NewsSection("International-CHINA", "https://www.jagran.com/world/china-news-hindi.html"),
//			new NewsSection("International-MIDDLE-EAST", "https://www.jagran.com/world/middle-east-news-hindi.html"),
//			new NewsSection("Entertainment-Bollywood", "https://www.jagran.com/entertainment/bollywood-news-hindi.html"),
//			new NewsSection("Entertainment-Web-Series", "https://www.jagran.com/entertainment/web-series-review-news-hindi.html"),
//			new NewsSection("Cricket-Bouncer", "https://www.jagran.com/cricket/bouncer-news-hindi.html"),
//			new NewsSection("Cricket-Headlines", "https://www.jagran.com/cricket/headlines-news-hindi.html"),
//			new NewsSection("Business", "https://www.jagran.com/business/biz-news-hindi.html")

	);

	public Ndtv(String root) {
		super(root, "ndtv");
	}

	@Override
	public void run() {
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		sections.forEach(section -> {
			checkOrCreateFolderAndFiles(section.getTopic());
			driver.get(section.getUrl());
			List<WebElement> elements = mainSection(driver); 
			List<WebElement> more = moreSection(driver);
			System.out.println(elements.size());

//			List<Article> articles =
			elements.stream().map(element -> getUrlDateAndTitle(element))
					// .filter(url -> url != null && !url.isBlank())
//					.limit(2)
					// .map(url -> getFullContent(url))
					.filter(article -> article != null && !article.getContent().isBlank())
					.filter(article -> isNewArticle(article.getDate())
							&& isUnreadArticle(article.getUrl(), section.getTopic()))
					.forEach(System.out::println);
//					.collect(Collectors.toList());
			// writeArticles(section.getTopic(), articles);
		});
		driver.quit();
	}

	private List<WebElement> moreSection(WebDriver driver) {
		List<WebElement> elements = new ArrayList<>();
		for (int i= 1; i<=3; i++) {
			try {
				driver.findElement(By.id("loadmorenews_btn")).findElement(By.className("btn_bm")).click();
				elements.addAll(driver.findElements(By.cssSelector("#loadmorenews ul li")));
				elements = filterWithUrl(elements);
				break;
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		System.out.println(elements);
		return elements;
	}

	private List<WebElement> mainSection(WebDriver driver) {
		List<WebElement> elements = new ArrayList<>();
		for (int i= 1; i<=3; i++) {
			try {
				elements.addAll(driver
						.findElements(By.cssSelector("div.vjl-cntr article div.NwsLstPg_hd ul.NwsLstPg_ul li")));
//						.findElements(By.cssSelector("div.vjl-cntr article div ul.NwsLstPg_ul li"));
				
				elements = filterWithUrl(elements);
				break;
			} catch(Exception ex) {
				ex.printStackTrace();				
			}
		}
		System.out.println(elements);
		return elements;
	}
	
	private List<WebElement> filterWithUrl(List<WebElement> elements) {
		return elements.stream().filter(ele -> {
			try {
				String url = ele.findElement(By.tagName("a")).getAttribute("href");
				if (StringUtils.isNotBlank(url) && url.startsWith("https://ndtv.in/")) {
					return true;
				} else {
					return false;
				}
			} catch (org.openqa.selenium.NoSuchElementException ex) {
				return false;
			}
		}).toList();
	} 

	private Article getUrlDateAndTitle(WebElement element) {
		Article article = new Article();
		try {
			String url = element.findElement(By.tagName("a")).getAttribute("href");
			article.setUrl(url);
			System.out.println(url);
			try {
				LocalDateTime dateTime = DateParser.parseDate(element.findElement(By.tagName("span")).getText());
				article.setDate(dateTime);
			} catch(DateTimeParseException ex) {
				LocalDateTime dateTime = DateParser.parseDate(element.findElement(By.cssSelector("span.pst-by_lnk")).getText());
				article.setDate(dateTime);
			}
			System.out.println(article.getDate());
			String title = element.findElement(By.tagName("h2 a")).getText();
			article.setTitle(title);
			System.out.println(title);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			return null;
		}
		return article;
	}

	private static Article getFullContent(Article article) {
		WebDriver driver = new ChromeDriver();
		driver.get(article.getUrl());
		try {
			WebElement element = driver.findElement(By.className("story-article"));
			wayOne(element, article);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			System.out.println("Not able to fetch news article with url " + article.getUrl());
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
			textString = element.findElement(By.className("full_article")).getText();
			article.setContent(textString);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			System.out.println("Not able to fetch news article details: " + article.getUrl());
		}
		try {
			textString = element.findElement(By.cssSelector("h2")).getText();
			article.setSynopsys(textString);
		} catch (org.openqa.selenium.NoSuchElementException ex) {
			System.out.println("Not able to fetch news article synopsis: " + article.getUrl());
		}
	}

	public class DateParser {
		private static final Map<String, String> hindiToEnglishMonths = new HashMap<>();
		static {
			hindiToEnglishMonths.put("जनवरी", "January");
			hindiToEnglishMonths.put("फरवरी", "February");
			hindiToEnglishMonths.put("मार्च", "March");
			hindiToEnglishMonths.put("अप्रैल", "April");
			hindiToEnglishMonths.put("मई", "May");
			hindiToEnglishMonths.put("जून", "June");
			hindiToEnglishMonths.put("जुलाई", "July");
			hindiToEnglishMonths.put("अगस्त", "August");
			hindiToEnglishMonths.put("सितंबर", "September");
			hindiToEnglishMonths.put("अक्टूबर", "October");
			hindiToEnglishMonths.put("नवंबर", "November");
			hindiToEnglishMonths.put("दिसंबर", "December");
		}

		// September 30, 2024 18:15 pm IST
		private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
				.appendPattern("MMMM dd, yyyy HH:mm a z").toFormatter(Locale.ENGLISH);

		public static LocalDateTime parseDate(String hindiDate) {
			for (Map.Entry<String, String> entry : hindiToEnglishMonths.entrySet()) {
				hindiDate = hindiDate.replace(entry.getKey(), entry.getValue());
			}

			LocalDateTime dateTime = LocalDateTime.parse(hindiDate, formatter);
			return dateTime;
		}

	}
}
