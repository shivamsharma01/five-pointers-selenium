package com.fivepointers.selenium;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class DriveSelenium {
	// September 16, 2024 07:38 IST

	public void run() {
//		WebDriver driver = new ChromeDriver();
//
//		driver.get("https://demo.opencart.com");
//
//		String title = driver.getTitle();
//
//		if ("Your Store".equals(title)) {
//			System.out.println("true");
//		} else {
//			System.out.println("false");
//		}
//		driver.findElement(By.xpath("//input[@name='search' or @placeholder='Search']")).sendKeys("T-Shirt");
//		driver.close();
		
		News news = new ExpressNews("news-data");
		news.run();
	}

	
	static class Article {
		String title;
		LocalDateTime date;
		String synopsys;
		String url;
		String content;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public LocalDateTime getDate() {
			return date;
		}

		public void setDate(LocalDateTime date) {
			this.date = date;
		}

		public String getSynopsys() {
			return synopsys;
		}

		public void setSynopsys(String synopsys) {
			this.synopsys = synopsys;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}
}
