package com.cleaning.freshup.domain.schedule.controller;

import com.cleaning.freshup.domain.schedule.dto.ScheduleRequestDto;
import com.cleaning.freshup.domain.schedule.dto.ScheduleResponseDto;
import com.cleaning.freshup.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 이 클래스가 REST API 요청을 처리하는 Controller라는 뜻이다.
// REST API Controller는 보통 JSON 데이터를 주고받을 때 사용한다.
@RestController

// 이 Controller의 기본 URL 주소를 설정한다.
// 즉, 이 클래스 안의 API들은 모두 /api/schedules 로 시작한다.
//
// 예:
// GET    /api/schedules/test
// GET    /api/schedules?year=2026&month=5
// POST   /api/schedules
// PUT    /api/schedules/{scheduleId}
// DELETE /api/schedules/{scheduleId}
@RequestMapping("/api/schedules")

// Lombok 어노테이션
// final 필드를 매개변수로 받는 생성자를 자동으로 만들어준다.
//
// 여기서는 아래의 scheduleService를 주입받는 생성자가 자동 생성된다.
// 즉, ScheduleService를 직접 new 하지 않아도 Spring이 넣어준다.
@RequiredArgsConstructor
public class ScheduleController {

    // 일정 관련 비즈니스 로직을 처리하는 Service이다.
    //
    // Controller는 요청을 받고 응답을 돌려주는 역할을 하고,
    // 실제 일정 조회/등록/수정/삭제 처리는 Service에서 담당한다.
    private final ScheduleService scheduleService;

    // GET 요청을 처리한다.
    //
    // 실제 요청 주소:
    // GET /api/schedules/test
    //
    // 역할:
    // Schedule API가 정상적으로 동작하는지 간단히 확인하는 테스트용 API이다.
    @GetMapping("/test")
    public String test() {

        // 문자열을 그대로 응답한다.
        //
        // 브라우저나 Postman에서 /api/schedules/test로 요청했을 때
        // "schedule api ok"가 나오면 API 연결이 된 것이다.
        return "schedule api ok";
    }

    // GET 요청을 처리한다.
    //
    // 실제 요청 주소:
    // GET /api/schedules?year=2026&month=5
    //
    // 역할:
    // 특정 연도와 월에 해당하는 일정 목록을 조회한다.
    @GetMapping
    public List<ScheduleResponseDto> getSchedules(
            // URL 쿼리스트링에서 year 값을 가져온다.
            //
            // 예:
            // /api/schedules?year=2026&month=5
            // → year에는 2026이 들어간다.
            @RequestParam int year,

            // URL 쿼리스트링에서 month 값을 가져온다.
            //
            // 예:
            // /api/schedules?year=2026&month=5
            // → month에는 5가 들어간다.
            @RequestParam int month
    ) {

        // Service에게 해당 연도와 월의 일정 목록 조회를 요청한다.
        //
        // 반환 타입이 List<ScheduleResponseDto>이므로
        // 여러 개의 일정 정보가 JSON 배열 형태로 응답된다.
        return scheduleService.getSchedules(year, month);
    }

    // POST 요청을 처리한다.
    //
    // 실제 요청 주소:
    // POST /api/schedules
    //
    // 역할:
    // 새로운 일정을 등록한다.
    @PostMapping
    public ScheduleResponseDto createSchedule(
            // 요청 body에 담긴 JSON 데이터를 ScheduleRequestDto 객체로 변환해서 받는다.
            //
            // 예:
            // {
            //   "title": "청소",
            //   "date": "2026-05-07"
            // }
            //
            // 이런 JSON 데이터가 requestDto에 담긴다.
            @RequestBody ScheduleRequestDto requestDto
    ) {

        // Service에게 일정 등록을 요청한다.
        //
        // requestDto에는 클라이언트가 보낸 일정 등록 정보가 들어있다.
        //
        // 등록된 일정 정보를 ScheduleResponseDto 형태로 반환한다.
        return scheduleService.createSchedule(requestDto);
    }

    // PUT 요청을 처리한다.
    //
    // 실제 요청 주소:
    // PUT /api/schedules/{scheduleId}
    //
    // 예:
    // PUT /api/schedules/3
    // → scheduleId가 3번인 일정을 수정한다.
    @PutMapping("/{scheduleId}")
    public ScheduleResponseDto updateSchedule(
            // URL 경로에 들어있는 scheduleId 값을 가져온다.
            //
            // 예:
            // /api/schedules/3 으로 요청하면
            // scheduleId에는 3이 들어간다.
            @PathVariable Long scheduleId,

            // 요청 body에 담긴 JSON 데이터를 ScheduleRequestDto 객체로 변환해서 받는다.
            //
            // 이 requestDto에는 수정할 일정 내용이 들어있다.
            @RequestBody ScheduleRequestDto requestDto
    ) {

        // Service에게 특정 일정 수정을 요청한다.
        //
        // scheduleId: 어떤 일정을 수정할지 식별하는 값
        // requestDto: 수정할 내용이 들어있는 객체
        //
        // 수정된 일정 정보를 ScheduleResponseDto 형태로 반환한다.
        return scheduleService.updateSchedule(scheduleId, requestDto);
    }

    // DELETE 요청을 처리한다.
    //
    // 실제 요청 주소:
    // DELETE /api/schedules/{scheduleId}
    //
    // 예:
    // DELETE /api/schedules/3
    // → scheduleId가 3번인 일정을 삭제한다.
    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(
            // URL 경로에 들어있는 scheduleId 값을 가져온다.
            //
            // 예:
            // /api/schedules/3 으로 요청하면
            // scheduleId에는 3이 들어간다.
            @PathVariable Long scheduleId
    ) {

        // Service에게 특정 일정 삭제를 요청한다.
        //
        // 반환 타입이 void이므로,
        // 삭제 후 별도의 데이터를 응답하지 않는다.
        scheduleService.deleteSchedule(scheduleId);
    }
}