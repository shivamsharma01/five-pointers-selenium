package com.fivepointers.selenium.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fivepointers.selenium.entity.NewsStore;

@Repository
public interface NewsStoreRepository extends JpaRepository<NewsStore, Long> {
	
	@Query("SELECT MAX(ns.scrapDate) FROM NewsStore ns")
	public LocalDateTime findLatestNewsArticleDateTime();
	
	@Query("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM NewsStore ns WHERE ns.url = :url")
    boolean existsByUrl(@Param("url") String url);
}