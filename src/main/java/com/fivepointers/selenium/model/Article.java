package com.fivepointers.selenium.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Article {
	String url;
	String title;
	String synopsys;
	String content;
	LocalDateTime publishDate;
	LocalDateTime saveDate;
	boolean isError;
}
