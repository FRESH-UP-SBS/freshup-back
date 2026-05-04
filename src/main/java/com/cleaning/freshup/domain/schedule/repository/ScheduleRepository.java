package com.cleaning.freshup.domain.schedule.repository;

import com.cleaning.freshup.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("""
            SELECT s
            FROM Schedule s
            JOIN FETCH s.user
            JOIN FETCH s.work
            WHERE s.date BETWEEN :start AND :end
            ORDER BY s.date ASC, s.id ASC
            """)
    List<Schedule> findSchedulesByMonth(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("""
        SELECT COUNT(s)
        FROM Schedule s
        WHERE s.date BETWEEN :start AND :end
        AND s.work.id IN (
            SELECT cr.work.id
            FROM CleaningRole cr
            WHERE cr.user.id = :userId
        )
        """)
    long countSchedulesByAssignedWorkAndWeek(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}