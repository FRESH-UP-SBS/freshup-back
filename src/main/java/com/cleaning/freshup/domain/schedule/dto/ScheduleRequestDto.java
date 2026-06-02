package com.cleaning.freshup.domain.schedule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 일정 등록 또는 일정 수정 요청을 받을 때 사용하는 DTO이다.
//
// DTO란?
// Data Transfer Object의 줄임말로,
// Controller, Service 같은 계층 사이에서 데이터를 주고받기 위해 사용하는 객체이다.
//
// 이 클래스는 보통 Controller에서 @RequestBody로 JSON 요청 데이터를 받을 때 사용된다.
@Getter

// Lombok 어노테이션
// 기본 생성자를 자동으로 만들어준다.
//
// @RequestBody로 JSON 데이터를 객체로 변환할 때
// 기본 생성자가 필요할 수 있다.
@NoArgsConstructor
public class ScheduleRequestDto {

    // 일정에 배정될 사용자의 고유 번호이다.
    //
    // 예:
    // userId가 1이면
    // 1번 사용자에게 일정을 배정한다는 의미이다.
    private Long userId;

    // 일정에 연결될 청소 업무의 고유 번호이다.
    //
    // 예:
    // workId가 2이면
    // 2번 청소 업무를 일정에 등록한다는 의미이다.
    private Long workId;

    // 일정 날짜를 저장하는 필드이다.
    //
    // LocalDate는 날짜만 표현하는 타입이다.
    // 시간 정보는 포함하지 않는다.
    //
    // 예:
    // 2026-05-07
    //
    // JSON 요청에서는 보통 아래처럼 전달된다.
    // {
    //   "userId": 1,
    //   "workId": 2,
    //   "date": "2026-05-07"
    // }
    private LocalDate date;
}