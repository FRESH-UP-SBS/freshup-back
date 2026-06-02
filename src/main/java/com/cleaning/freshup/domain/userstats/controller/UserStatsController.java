package com.cleaning.freshup.domain.userstats.controller;

import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;
import com.cleaning.freshup.domain.userstats.dto.MyPageStatsResponseDto;
import com.cleaning.freshup.domain.userstats.service.UserStatsService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// 이 클래스가 REST API 요청을 처리하는 Controller라는 뜻이다.
// REST API Controller는 보통 JSON 데이터를 주고받을 때 사용한다.
@RestController

// Lombok 어노테이션
// final 필드를 매개변수로 받는 생성자를 자동으로 만들어준다.
//
// 여기서는 아래의 userStatsService, userRepository를
// 주입받는 생성자가 자동 생성된다.
//
// 즉, Service나 Repository를 직접 new 하지 않아도
// Spring이 알아서 넣어준다.
@RequiredArgsConstructor

// 이 Controller의 기본 URL 주소를 설정한다.
// 즉, 이 클래스 안의 API들은 모두 /api/user-stats 로 시작한다.
//
// 예:
// GET /api/user-stats/me
// GET /api/user-stats/{userId}
@RequestMapping("/api/user-stats")
public class UserStatsController {

    // 사용자 통계 관련 비즈니스 로직을 처리하는 Service이다.
    //
    // Controller는 요청을 받고 응답을 돌려주는 역할을 하고,
    // 실제 통계 계산이나 조회 로직은 Service에서 담당한다.
    private final UserStatsService userStatsService;

    // 사용자 정보를 DB에서 조회할 때 사용하는 Repository이다.
    //
    // 여기서는 로그인한 사용자의 email로
    // User Entity를 찾기 위해 사용한다.
    private final UserRepository userRepository;

    // GET 요청을 처리한다.
    //
    // 실제 요청 주소:
    // GET /api/user-stats/me
    //
    // 역할:
    // 현재 로그인한 사용자의 마이페이지 통계 정보를 조회한다.
    @GetMapping("/me")
    public MyPageStatsResponseDto getMyStats(
            // 현재 로그인한 사용자의 인증 정보를 가져온다.
            //
            // 이 프로젝트에서는 AuthenticationPrincipal 값으로
            // email 문자열이 들어온다고 가정하고 있다.
            //
            // 예:
            // 로그인한 사용자의 이메일이 test@test.com이면
            // email 변수에 "test@test.com"이 들어간다.
            @AuthenticationPrincipal String email
    ) {

        // 로그인한 사용자의 email을 이용해서 User 정보를 DB에서 찾는다.
        //
        // findByEmail(email)
        // → email이 일치하는 사용자를 조회한다.
        //
        // orElseThrow(...)
        // → 해당 사용자가 없으면 404 NOT_FOUND 오류를 발생시킨다.
        //
        // ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음")
        // → 클라이언트에게 "사용자 없음"이라는 메시지와 함께
        //   404 상태 코드를 응답한다.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음"));

        // 조회한 User의 id를 이용해서
        // 해당 사용자의 마이페이지 통계 정보를 Service에 요청한다.
        //
        // user.getId()
        // → 로그인한 사용자의 고유 번호를 가져온다.
        //
        // 반환되는 MyPageStatsResponseDto는
        // 클라이언트에게 JSON 형태로 응답된다.
        return userStatsService.getMyPageStats(user.getId());
    }

    // GET 요청을 처리한다.
    //
    // 실제 요청 주소:
    // GET /api/user-stats/{userId}
    //
    // 예:
    // GET /api/user-stats/3
    // → userId가 3번인 사용자의 통계 정보를 조회한다.
    @GetMapping("/{userId}")
    public MyPageStatsResponseDto getUserStats(
            // URL 경로에 들어있는 userId 값을 가져온다.
            //
            // 예:
            // /api/user-stats/3 으로 요청하면
            // userId에는 3이 들어간다.
            @PathVariable Long userId
    ) {

        // Service에게 특정 사용자의 마이페이지 통계 조회를 요청한다.
        //
        // userId:
        // 어떤 사용자의 통계를 조회할지 식별하는 값이다.
        //
        // 반환되는 MyPageStatsResponseDto는
        // 클라이언트에게 JSON 형태로 응답된다.
        return userStatsService.getMyPageStats(userId);
    }
}