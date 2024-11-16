package com.fivepointers.selenium.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fivepointers.selenium.entity.Scheduler;
import com.fivepointers.selenium.repository.SchedulerRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Service
public class SchedulerService {

	private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);
	private SchedulerRepository schedulerRepository;

	public Scheduler createScheduler() {
		Scheduler entity = new Scheduler();
		entity.setDuration(0);
		entity.setScrapStartTime(LocalDateTime.now());
		entity.setSuccess(false);
		return schedulerRepository.save(entity);
	}

	public void updateScheduler(Scheduler scheduler, boolean isSuccess) {
		if (scheduler.getId() <= 0) {
			log.error("invalid scheduler id: " + scheduler.getDuration());
			throw new RuntimeException("invalid scheduler id: " + scheduler.getDuration());
		}
		if (scheduler.getScrapStartTime() == null) {
			throw new RuntimeException("invalid scheduler startTime: null");
		}
		LocalDateTime now = LocalDateTime.now();
		if (scheduler.getScrapStartTime().isAfter(now)) {
			throw new RuntimeException(
					"invalid scheduler startTime: " + scheduler.getScrapStartTime() + " is a future time.");
		}

		schedulerRepository.updateScheduler(scheduler.getId(),
				Duration.between(scheduler.getScrapStartTime(), now).get(ChronoUnit.SECONDS), isSuccess);
	}
}
