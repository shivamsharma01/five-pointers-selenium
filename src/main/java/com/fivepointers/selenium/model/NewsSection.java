package com.fivepointers.selenium.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class NewsSection {
	private String category;
	private String tag;
	private String url;
}