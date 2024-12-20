package com.fivepointers.selenium.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fivepointers.selenium.entity.Scheduler;
import com.fivepointers.selenium.service.AbstractNewsService;
import com.fivepointers.selenium.service.ExpressNewsService;
import com.fivepointers.selenium.service.JagranNewsService;
import com.fivepointers.selenium.service.NavbharatNewsService;
import com.fivepointers.selenium.service.NewsStoreService;
import com.fivepointers.selenium.service.SchedulerService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class NewsScrapTask {

	public static LocalDateTime lastScraped;
	private static final Logger log = LoggerFactory.getLogger(NewsScrapTask.class);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private static boolean isSuccess;

	@Value("#{'${selenium.chrome.arguments}'.split(',')}")
	private List<String> arguments;

	private final NewsStoreService newsStoreService;

	private final SchedulerService schedulerService;
	private final ExpressNewsService expressNewsService;
	private final JagranNewsService jagranNewsService;
	private final NavbharatNewsService navbharatNewsService;

	@Scheduled(fixedRateString = "${news.scrap.scheduler.fetch.rate}", initialDelay = 60000)
	public void reportCurrentTime() {
		Scheduler scheduler = schedulerService.createScheduler();
		WebDriver driver;
		if (arguments != null && !arguments.isEmpty()) {
			ChromeOptions options = new ChromeOptions();
			for (String argument : arguments) {
				if (argument != null && !argument.trim().isEmpty()) {
					options.addArguments(argument);
				}
			}
			driver = new ChromeDriver(options);
		} else {
			driver = new ChromeDriver();
		}

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
		driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		isSuccess = true;
		findLastScraped();
		scrapNews(scheduler, driver, wait, expressNewsService);
		scrapNews(scheduler, driver, wait, jagranNewsService);
		scrapNews(scheduler, driver, wait, navbharatNewsService);
		schedulerService.updateScheduler(scheduler, isSuccess);
		driver.quit();
	}

	public void scrapNews(Scheduler scheduler, WebDriver driver, WebDriverWait wait, AbstractNewsService newsService) {
		LocalDateTime now = LocalDateTime.now();
		log.info("Starting News Scraping for " + newsService.getNEWS_CHANNEL_NAME() + ". Time is: "
				+ formatter.format(now));
		try {
			newsService.scrapNews(driver, wait, scheduler.getId());
		} catch (Exception e) {
			log.error("encountered error while scrapping news from " + newsService.getNEWS_CHANNEL_NAME() + " : "
					+ e.getMessage());
			isSuccess = false;
		}
		log.info("Scraped News data for " + newsService.getNEWS_CHANNEL_NAME() + ". Time is: " + formatter.format(now));
	}

	private void findLastScraped() {
		lastScraped = newsStoreService.findLatestNewsArticleDateTime();
	}

}
