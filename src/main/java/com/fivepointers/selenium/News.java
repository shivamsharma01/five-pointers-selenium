package com.fivepointers.selenium;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.fivepointers.selenium.DriveSelenium.Article;

public abstract class News {
	private final String root;
	private final String org;
	private final LocalDateTime date;
	
	public News(String root, String org) {
		this.root = root;
		this.org = org;
		this.date = LocalDateTime.now().minusDays(1);
	}
	
	public abstract void run();

	protected boolean isNewArticle(LocalDateTime articleDate) {
		return !Duration.between(date, articleDate).isNegative();
	}
	
	protected boolean isUnreadArticle(String url, String topic) {
		try {
			List<String> urls = Files.readAllLines(getUrlFilePath(topic)).stream().map(line -> line.trim()).toList();
			boolean b = !urls.contains(url);
			return b;
		} catch (IOException e) {
			return true;
		}
	}
	
	protected void checkOrCreateFolderAndFiles(String topic) {
		Path folderPath = getDir(topic);
		if (Files.notExists(folderPath)) {
			try {
				Files.createDirectories(folderPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Path titleFilePath = getUrlFilePath(topic);
		if (Files.notExists(titleFilePath)) {
			try {
				Files.createFile(titleFilePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	private Path getDir(String topic) {
		return Path
				.of(String.format("%s/%s/%s/%s/", root, org, topic, date.getDayOfMonth() + "-" + date.getMonth().name())
						.replaceAll("//", "/"));
	}
	
	private Path getUrlFilePath(String topic) {
		return Path.of(getDir(topic) + "/url.txt");
	}

	protected void writeArticles(String topic, List<Article> articles) {
		StringBuilder builder = new StringBuilder("");
		articles.forEach(a -> {
			builder.append("Title:" + a.getTitle() + "\n");
			builder.append("Date: " + a.getDate() + "\n");
			builder.append("Synopsys: " + a.getSynopsys() + "\n");
			builder.append("Url: " + a.getUrl() + "\n");
			builder.append("Content: \n" + a.getContent() + "\n");
			builder.append("==============================\n");
			try {
				Files.writeString(getUrlFilePath(topic), a.getUrl() + "\n", StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		try {
			Path filePath = Path.of(getDir(topic) + "/news.txt");
			if (Files.notExists(filePath)) {
				Files.createFile(filePath);
			}
			Files.writeString(filePath, builder.toString(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
