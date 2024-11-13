package com.fivepointers.selenium.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fivepointers.selenium.repository.NewsStoreRepository;
import com.fivepointers.selenium.service.ExpressNewsService;
import com.fivepointers.selenium.service.JagranNewsService;

@Configuration
public class NewsServiceConfig {

	@Bean
	ExpressNewsService expressNewsService(NewsStoreRepository newsStoreRepository) {
		return new ExpressNewsService("Express News", newsStoreRepository);
	}

	@Bean
	JagranNewsService jagranNewsService(NewsStoreRepository newsStoreRepository) {
		return new JagranNewsService("Jagran", newsStoreRepository);
	}
}
