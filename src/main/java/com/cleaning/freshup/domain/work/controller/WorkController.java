package com.cleaning.freshup.domain.work.controller;

import com.cleaning.freshup.domain.work.dto.WorkRequestDto;
import com.cleaning.freshup.domain.work.dto.WorkResponseDto;
import com.cleaning.freshup.domain.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 이 클래스가 REST API 요청을 처리하는 Controller라는 뜻이다.
// REST API Controller는 보통 JSON 데이터를 주고받을 때 사용한다.
@RestController

// 이 Controller의 기본 URL 주소를 설정한다.
// 즉, 이 클래스 안의 API들은 모두 /api/works 로 시작한다.
//
// 예:
// GET    /api/works
// POST   /api/works
// PUT    /api/works/{workId}
// DELETE /api/works/{workId}
@RequestMapping("/api/works")

// Lombok 어노테이션
// final 필드를 매개변수로 받는 생성자를 자동으로 만들어준다.
//
// 여기서는 아래의 workService를 주입받는 생성자가 자동 생성된다.
// 즉, WorkService를 직접 new 하지 않아도 Spring이 넣어준다.
@RequiredArgsConstructor
public class WorkController {

    // 청소 업무 관련 비즈니스 로직을 처리하는 Service이다.
    //
    // Controller는 요청을 받고 응답을 돌려주는 역할을 하고,
    // 실제 청소 업무 조회/등록/수정/삭제 처리는 Service에서 담당한다.
    private final WorkService workService;

    // GET 요청을 처리한다.
    //
    // 실제 요청 주소:
    // GET /api/works
    //
    // 역할:
    // 청소 업무 목록을 조회한다.
    @GetMapping
    public List<WorkResponseDto> getWorks() {

        // Service에게 청소 업무 목록 조회를 요청한다.
        //
        // 반환 타입이 List<WorkResponseDto>이므로
        // 여러 개의 청소 업무 정보가 JSON 배열 형태로 응답된다.
        return workService.getWorks();
    }

    // POST 요청을 처리한다.
    //
    // 실제 요청 주소:
    // POST /api/works
    //
    // 역할:
    // 새로운 청소 업무를 등록한다.
    @PostMapping
    public WorkResponseDto createWork(
            // 요청 body에 담긴 JSON 데이터를 WorkRequestDto 객체로 변환해서 받는다.
            //
            // 예:
            // {
            //   "workName": "화장실 청소"
            // }
            //
            // 이런 JSON 데이터가 requestDto에 담긴다.
            @RequestBody WorkRequestDto requestDto
    ) {

        // Service에게 청소 업무 등록을 요청한다.
        //
        // requestDto에는 클라이언트가 보낸 청소 업무 등록 정보가 들어있다.
        //
        // 등록된 청소 업무 정보를 WorkResponseDto 형태로 반환한다.
        return workService.createWork(requestDto);
    }

    // PUT 요청을 처리한다.
    //
    // 실제 요청 주소:
    // PUT /api/works/{workId}
    //
    // 예:
    // PUT /api/works/3
    // → workId가 3번인 청소 업무를 수정한다.
    @PutMapping("/{workId}")
    public WorkResponseDto updateWork(
            // URL 경로에 들어있는 workId 값을 가져온다.
            //
            // 예:
            // /api/works/3 으로 요청하면
            // workId에는 3이 들어간다.
            @PathVariable Long workId,

            // 요청 body에 담긴 JSON 데이터를 WorkRequestDto 객체로 변환해서 받는다.
            //
            // 이 requestDto에는 수정할 청소 업무 내용이 들어있다.
            @RequestBody WorkRequestDto requestDto
    ) {

        // Service에게 특정 청소 업무 수정을 요청한다.
        //
        // workId:
        // 어떤 청소 업무를 수정할지 식별하는 값이다.
        //
        // requestDto:
        // 수정할 내용이 들어있는 객체이다.
        //
        // 수정된 청소 업무 정보를 WorkResponseDto 형태로 반환한다.
        return workService.updateWork(workId, requestDto);
    }

    // DELETE 요청을 처리한다.
    //
    // 실제 요청 주소:
    // DELETE /api/works/{workId}
    //
    // 예:
    // DELETE /api/works/3
    // → workId가 3번인 청소 업무를 삭제한다.
    @DeleteMapping("/{workId}")
    public void deleteWork(
            // URL 경로에 들어있는 workId 값을 가져온다.
            //
            // 예:
            // /api/works/3 으로 요청하면
            // workId에는 3이 들어간다.
            @PathVariable Long workId
    ) {

        // Service에게 특정 청소 업무 삭제를 요청한다.
        //
        // 반환 타입이 void이므로,
        // 삭제 후 별도의 데이터를 응답하지 않는다.
        workService.deleteWork(workId);
    }
}