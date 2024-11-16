package com.fivepointers.selenium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fivepointers.selenium.entity.Scheduler;

@Repository
public interface SchedulerRepository extends JpaRepository<Scheduler, Long> {

	@Transactional
	@Modifying
	@Query("update Scheduler set duration = :duration, isSuccess = :isSuccess where id = :id")
	public void updateScheduler(@Param("id") long id, @Param("duration") long duration,
			@Param("isSuccess") boolean isSuccess);
}