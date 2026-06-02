package com.cleaning.freshup.domain.schedule.repository;

import com.cleaning.freshup.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

// Schedule Entity를 DB에서 조회, 저장, 삭제할 때 사용하는 Repository이다.
//
// JpaRepository<Schedule, Long>의 의미:
// - Schedule: 이 Repository가 관리할 Entity 클래스
// - Long: Schedule Entity의 기본키(id) 타입
//
// JpaRepository를 상속하면 기본적인 DB 기능을 자동으로 사용할 수 있다.
// 예:
// - save()
// - findById()
// - findAll()
// - delete()
// - count()
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 특정 기간에 해당하는 일정 목록을 조회하는 메서드이다.
    //
    // 메서드 이름은 findSchedulesByMonth이지만,
    // 실제로는 start 날짜부터 end 날짜 사이의 일정을 조회한다.
    //
    // 예:
    // start = 2026-05-01
    // end = 2026-05-31
    // → 2026년 5월 일정 목록 조회
    @Query("""
            SELECT s
            FROM Schedule s
            JOIN FETCH s.user
            JOIN FETCH s.work
            WHERE s.date BETWEEN :start AND :end
            ORDER BY s.date ASC, s.id ASC
            """)
    List<Schedule> findSchedulesByMonth(
            // JPQL 쿼리 안의 :start 값에
            // 이 매개변수 start 값을 넣겠다는 뜻이다.
            //
            // 조회 시작 날짜이다.
            @Param("start") LocalDate start,

            // JPQL 쿼리 안의 :end 값에
            // 이 매개변수 end 값을 넣겠다는 뜻이다.
            //
            // 조회 종료 날짜이다.
            @Param("end") LocalDate end
    );

    // 특정 사용자가 맡은 청소 업무가
    // 특정 기간 안에 일정으로 몇 개 등록되어 있는지 세는 메서드이다.
    //
    // 쉽게 말하면:
    // "이 사용자가 담당자인 업무들이 이번 주 일정에 몇 번 들어가 있는지"
    // 확인하는 쿼리이다.
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
            // JPQL 쿼리 안의 :userId 값에
            // 이 매개변수 userId 값을 넣겠다는 뜻이다.
            //
            // 확인할 사용자의 고유 번호이다.
            @Param("userId") Long userId,

            // JPQL 쿼리 안의 :start 값에
            // 이 매개변수 start 값을 넣겠다는 뜻이다.
            //
            // 조회 시작 날짜이다.
            @Param("start") LocalDate start,

            // JPQL 쿼리 안의 :end 값에
            // 이 매개변수 end 값을 넣겠다는 뜻이다.
            //
            // 조회 종료 날짜이다.
            @Param("end") LocalDate end
    );
}