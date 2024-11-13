package com.fivepointers.selenium.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fivepointers.selenium.repository.NewsStoreRepository;
import com.fivepointers.selenium.service.ExpressNewsService;

@Configuration
public class NewsServiceConfig {

	@Bean
	ExpressNewsService expressNewsService(NewsStoreRepository newsStoreRepository) {
		return new ExpressNewsService("Express News", newsStoreRepository);
	}
}
