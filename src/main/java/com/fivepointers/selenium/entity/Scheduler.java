package com.fivepointers.selenium.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "scheduler_entry")
@Entity
public class Scheduler {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "scrap_start_datetime", nullable = false)
	private LocalDateTime scrapStartTime;
	@Column(name = "duration", nullable = false)
	private long duration;
	@Column(name = "is_processing_success", nullable = false)
	private boolean isSuccess;
}