package com.cleaning.freshup.domain.work.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 청소 업무를 등록하거나 수정할 때
// 클라이언트가 서버로 보내는 요청 데이터를 담는 DTO이다.
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
public class WorkRequestDto {

    // 청소 업무 이름을 저장하는 필드이다.
    //
    // 예:
    // "설거지"
    // "화장실 청소"
    // "바닥 청소"
    //
    // JSON 요청에서는 보통 아래처럼 전달된다.
    // {
    //   "workName": "화장실 청소"
    // }
    private String workName;

    // 이 청소 업무를 담당할 사용자들의 고유 번호 목록이다.
    //
    // List<Long>의 의미:
    // - List: 여러 개의 값을 담을 수 있는 목록
    // - Long: 사용자 id 값의 타입
    //
    // 예:
    // memberIds = [1, 2, 3]
    // → 1번, 2번, 3번 사용자를 이 청소 업무 담당자로 지정한다는 의미이다.
    //
    // JSON 요청에서는 보통 아래처럼 전달된다.
    // {
    //   "workName": "화장실 청소",
    //   "memberIds": [1, 2, 3]
    // }
    private List<Long> memberIds;
}