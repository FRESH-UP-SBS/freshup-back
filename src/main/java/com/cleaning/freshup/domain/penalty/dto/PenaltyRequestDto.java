package com.cleaning.freshup.domain.penalty.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 클라이언트가 벌금 정보를 수정할 때 보내는 요청 데이터를 담는 DTO이다.
//
// DTO란?
// Data Transfer Object의 줄임말로,
// 계층 간 데이터를 주고받기 위해 사용하는 객체이다.
//
// 이 클래스는 주로 Controller에서 @RequestBody로 JSON 요청 값을 받을 때 사용된다.
@Getter

// Lombok 어노테이션
// 기본 생성자를 자동으로 만들어준다.
//
// @RequestBody로 JSON 데이터를 객체로 변환할 때
// 기본 생성자가 필요할 수 있다.
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyRequestDto {

    // 벌금 조정 여부를 나타내는 값이다.
    //
    // 예:
    // "Y" → 조정됨
    // "N" → 조정되지 않음
    //
    // 실제 의미는 프로젝트에서 정한 규칙에 따라 사용된다.
    private String adjustmentYn;

    private List<Long> assignees; // 담당자 ID 목록
    private String paymentStatus; // 수납 여부 (ALL, Y, N)
    private Integer amount;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate; // 시작 날짜
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate; // 종료 날짜

}