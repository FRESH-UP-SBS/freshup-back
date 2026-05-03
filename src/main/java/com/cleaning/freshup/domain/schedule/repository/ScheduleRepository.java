package com.cleaning.freshup.domain.schedule.repository;

import com.cleaning.freshup.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByDateBetween(LocalDate start, LocalDate end);
}