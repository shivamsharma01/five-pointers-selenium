package com.fivepointers.selenium.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fivepointers.selenium.service.NewsStoreService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class NewsDeleteTask {

	public static LocalDateTime lastScraped;
	private static final Logger log = LoggerFactory.getLogger(NewsDeleteTask.class);

	private final NewsStoreService newsStoreService;

	@Scheduled(fixedRateString = "${news.scrap.scheduler.delete.rate}", initialDelay = 60000)
	public void reportCurrentTime() {
		log.info("NewsDeleteTask: current time is " + LocalDateTime.now());
		newsStoreService.deleteByDuration(LocalDate.now().minusDays(2).atStartOfDay());
	}
}
