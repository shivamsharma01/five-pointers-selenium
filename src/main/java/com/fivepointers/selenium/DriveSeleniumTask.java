package com.fivepointers.selenium;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fivepointers.selenium.repository.NewsStoreRepository;
import com.fivepointers.selenium.service.AbstractNewsService;
import com.fivepointers.selenium.service.ExpressNewsService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class DriveSeleniumTask {

	private static final Logger log = LoggerFactory.getLogger(DriveSeleniumTask.class);

	private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
			.appendPattern("dd-MMM-yyyy H:m:s z").toFormatter(Locale.ENGLISH);

	public static LocalDateTime lastScraped;

	private final NewsStoreRepository newsStoreRepository;
	private final ExpressNewsService expressNewsService;

	@Scheduled(fixedRateString = "${news.scrap.scheduler.rate}", initialDelay = 60000)
	public void reportCurrentTime() {
		findLastScraped();
		scrapNews(expressNewsService);
	}

	public void scrapNews(AbstractNewsService newsService) {
		LocalDateTime now = LocalDateTime.now();
		log.info("Starting News Scraping for " + newsService.getNEWS_CHANNEL_NAME() + ". Time is: "
				+ formatter.format(now));
		try {
			newsService.scrapNews();
		} catch (Exception e) {
			log.error("encountered error while scrapping news from " + newsService.getNEWS_CHANNEL_NAME());
		}
	}

	private void findLastScraped() {
		lastScraped = newsStoreRepository.findLatestNewsArticleDateTime();
		if (lastScraped == null) {
			lastScraped = LocalDateTime.now().minus(2, ChronoUnit.DAYS);
		}
	}

}
