package com.fivepointers.selenium.model;

public class NewsSection {
	private String topic;
	private String url;

	public NewsSection(String topic, String url) {
		this.topic = topic;
		this.url = url;
	}

	public String getTopic() {
		return topic;
	}

	public String getUrl() {
		return url;
	}
}