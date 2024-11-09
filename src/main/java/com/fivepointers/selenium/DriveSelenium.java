package com.fivepointers.selenium;

import java.time.LocalDateTime;

//@Service
public class DriveSelenium {


	public void textRun() {
		News news;
//		news = new ExpressNews("news-data");
//		news.run();
//		news = new Jagran("news-data");
//		news.run();
//		news = new Navbharat("news-data");
//		news.run();
		news = new Ndtv("");
		news.run();
	}
	
	public void run() {
//	    handler.addDate(sheet, 1, 0, LocalDateTime.now()); // Add data at row 1
//	    handler.getCell(handler.getRow(sheet, 1), 1).setCellValue("Sample News Title");
//	    handler.getCell(handler.getRow(sheet, 1), 2).setCellValue("Sample description for the news.");
//	    handler.getCell(handler.getRow(sheet, 1), 3).setCellValue("Author Name");
//	    handler.save();
		textRun();

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

		@Override
		public String toString() {
			return "Article [title=" + title + ", date=" + date + ", synopsys=" + synopsys + ", url=" + url
					+ ", content=" + content + "]";
		}
		
	}
}
