package com.cleaning.freshup.domain.penalty.controller;

import com.cleaning.freshup.domain.penalty.dto.PenaltyResponseDto;
import com.cleaning.freshup.domain.penalty.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.cleaning.freshup.domain.penalty.dto.PenaltyRequestDto;

import java.util.List;

// 이 클래스가 REST API 요청을 처리하는 Controller라는 뜻이다.
// REST API Controller는 보통 JSON 데이터를 주고받을 때 사용한다.
@RestController

// 이 Controller의 기본 URL 주소를 설정한다.
// 즉, 이 클래스 안의 API들은 모두 /api/penalties 로 시작한다.
//
// 예:
// GET  /api/penalties
// PUT  /api/penalties/{penaltyId}
@RequestMapping("/api/penalties")

// Lombok 어노테이션
// final 필드를 매개변수로 받는 생성자를 자동으로 만들어준다.
//
// 여기서는 아래의 penaltyService를 주입받는 생성자가 자동 생성된다.
// 즉, PenaltyService를 직접 new 하지 않아도 Spring이 넣어준다.
@RequiredArgsConstructor
public class PenaltyController {

    // 벌금 관련 비즈니스 로직을 처리하는 Service이다.
    //
    // Controller는 요청을 받고 응답을 돌려주는 역할을 하고,
    // 실제 조회/수정 같은 핵심 처리는 Service에서 담당한다.
    private final PenaltyService penaltyService;

    // GET 요청을 처리한다.
    //
    // 기본 주소가 /api/penalties 이므로
    // 이 메서드의 실제 요청 주소는 GET /api/penalties 이다.
    //
    // 역할:
    // 벌금 목록을 조회한다.
    @GetMapping
    public List<PenaltyResponseDto> getPenalties() {

        // Service에게 벌금 목록 조회를 요청하고,
        // 그 결과를 그대로 클라이언트에게 반환한다.
        //
        // 반환 타입이 List<PenaltyResponseDto>이므로
        // 여러 개의 벌금 정보를 JSON 배열 형태로 응답한다.
        return penaltyService.getPenalties();
    }

    // PUT 요청을 처리한다.
    //
    // 실제 요청 주소:
    // PUT /api/penalties/{penaltyId}
    //
    // 예:
    // PUT /api/penalties/1
    // → penaltyId가 1번인 벌금 정보를 수정한다.
    @PutMapping("/{penaltyId}")
    public PenaltyResponseDto updatePenalty(
            // URL 경로에 들어있는 penaltyId 값을 가져온다.
            //
            // 예:
            // /api/penalties/1 로 요청하면
            // penaltyId에는 1이 들어간다.
            @PathVariable Long penaltyId,

            // 요청 body에 담긴 JSON 데이터를 PenaltyRequestDto 객체로 변환해서 받는다.
            //
            // 예:
            // {
            //   "amount": 5000
            // }
            //
            // 이런 JSON 데이터가 requestDto에 담긴다.
            @RequestBody PenaltyRequestDto requestDto
    ) {

        // Service에게 특정 벌금 정보 수정을 요청한다.
        //
        // penaltyId: 어떤 벌금을 수정할지 식별하는 값
        // requestDto: 수정할 내용이 들어있는 객체
        //
        // 수정된 결과를 PenaltyResponseDto 형태로 반환한다.
        return penaltyService.updatePenalty(penaltyId, requestDto);
    }
}