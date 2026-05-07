package com.cleaning.freshup.domain.userstats.dto;

import lombok.Builder;
import lombok.Getter;

// 마이페이지에 보여줄 사용자 통계 정보를 담는 응답용 DTO이다.
//
// DTO란?
// Data Transfer Object의 줄임말로,
// Controller, Service 같은 계층 사이에서 데이터를 주고받기 위해 사용하는 객체이다.
//
// 이 클래스는 서버가 클라이언트에게
// 마이페이지 통계 데이터를 JSON 형태로 응답할 때 사용된다.
@Getter

// Lombok 어노테이션
// Builder 패턴을 사용할 수 있게 해준다.
//
// Builder 패턴을 사용하면 객체를 만들 때
// 어떤 필드에 어떤 값을 넣는지 보기 쉬워진다.
//
// 예:
// MyPageStatsResponseDto.builder()
//      .userSeq(1L)
//      .name("홍길동")
//      .role("USER")
//      .build();
@Builder
public class MyPageStatsResponseDto {

    // 사용자의 고유 번호이다.
    //
    // User Entity의 id 값이 들어간다.
    private Long userSeq;

    // 사용자 이름이다.
    //
    // 마이페이지에서 사용자 이름을 보여줄 때 사용한다.
    private String name;

    // 사용자의 권한 또는 역할이다.
    //
    // 예:
    // "USER" → 일반 사용자
    // "ADMIN" → 관리자
    //
    // 실제 값은 프로젝트에서 정한 User 역할 값에 따라 달라질 수 있다.
    private String role;

    // 이번 주에 사용자가 완료했거나 배정된 청소 횟수이다.
    //
    // long 타입은 정수 숫자를 저장하는 타입이다.
    // int보다 더 큰 숫자를 저장할 수 있다.
    private long weeklyCleanCount;

    // 이번 주에 아직 남아 있는 청소 횟수이다.
    //
    // 예:
    // 총 해야 할 청소가 5번이고,
    // 이미 완료/배정된 청소가 2번이면
    // 남은 청소 횟수는 3번처럼 계산될 수 있다.
    //
    // 실제 계산 방식은 Service 로직에서 결정된다.
    private long remainingCleanCount;

    // 사용자의 전체 벌금 금액이다.
    //
    // 정산 여부와 상관없이
    // 해당 사용자에게 발생한 벌금 총합을 담는다.
    //
    // 예:
    // 5000, 10000
    private int totalPenaltyAmount;

    // 사용자의 미정산 벌금 금액이다.
    //
    // 아직 정산되지 않은 벌금 금액의 합계를 담는다.
    //
    // 예:
    // 정산 여부가 "N"인 벌금만 합산한 금액
    private int unpaidPenaltyAmount;
}