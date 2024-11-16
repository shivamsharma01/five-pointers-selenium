package com.fivepointers.selenium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fivepointers.selenium.entity.NewsStoreError;
import com.google.common.base.Optional;

@Repository
public interface NewsStoreErrorRepository extends JpaRepository<NewsStoreError, Long> {

	@Query("SELECT CASE WHEN COUNT(*) <= :limit THEN true ELSE false END FROM NewsStoreError nse WHERE nse.url = :url")
	boolean isWithinRetryLimit(@Param("url") String url, @Param("limit") int retryLimit);

	Optional<NewsStoreError> findByUrl(String url);

}