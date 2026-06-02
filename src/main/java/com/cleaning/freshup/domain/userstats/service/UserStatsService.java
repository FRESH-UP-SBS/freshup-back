package com.cleaning.freshup.domain.userstats.service;

import com.cleaning.freshup.domain.penalty.repository.PenaltyRepository;
import com.cleaning.freshup.domain.schedule.repository.ScheduleRepository;
import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;
import com.cleaning.freshup.domain.userstats.dto.MyPageStatsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

// 이 클래스가 Service 역할을 하는 클래스라는 뜻이다.
//
// Service는 Controller와 Repository 사이에서
// 실제 비즈니스 로직을 처리하는 계층이다.
//
// 예:
// Controller → 요청 받기
// Service → 실제 로직 처리
// Repository → DB 접근
@Service

// Lombok 어노테이션
// final이 붙은 필드를 매개변수로 받는 생성자를 자동으로 만들어준다.
//
// 여기서는 아래의 userRepository, scheduleRepository, penaltyRepository를
// 주입받는 생성자가 자동 생성된다.
//
// 즉, Repository 객체를 직접 new 하지 않아도
// Spring이 알아서 넣어준다.
@RequiredArgsConstructor
public class UserStatsService {

    // 사용자 정보를 DB에서 조회할 때 사용하는 Repository이다.
    //
    // 마이페이지 통계를 만들기 전에
    // userId에 해당하는 사용자가 실제로 존재하는지 확인하기 위해 사용한다.
    private final UserRepository userRepository;

    // 일정 정보를 DB에서 조회할 때 사용하는 Repository이다.
    //
    // 여기서는 사용자의 이번 주 청소 일정 개수를 구할 때 사용한다.
    private final ScheduleRepository scheduleRepository;

    // 벌금 정보를 DB에서 조회할 때 사용하는 Repository이다.
    //
    // 여기서는 사용자의 전체 벌금 금액과
    // 미정산 벌금 금액을 구할 때 사용한다.
    private final PenaltyRepository penaltyRepository;

    // 특정 사용자의 마이페이지 통계 정보를 조회하는 메서드이다.
    //
    // userId를 받아서 아래 정보들을 계산한 뒤 DTO로 반환한다.
    //
    // 반환 정보 예:
    // - 사용자 번호
    // - 사용자 이름
    // - 사용자 권한
    // - 이번 주 청소 횟수
    // - 남은 청소 횟수
    // - 전체 벌금 금액
    // - 미정산 벌금 금액
    public MyPageStatsResponseDto getMyPageStats(Long userId) {

        // userId에 해당하는 사용자를 DB에서 조회한다.
        //
        // findById(userId)
        // → userId와 같은 id를 가진 User를 찾는다.
        //
        // orElseThrow(...)
        // → 해당 사용자가 없으면 예외를 발생시킨다.
        //
        // 예:
        // userId가 3이면 id가 3인 사용자를 찾는다.
        // 없으면 "존재하지 않는 회원입니다." 오류가 발생한다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 오늘 날짜를 구한다.
        //
        // LocalDate.now()
        // → 현재 날짜를 가져온다.
        //
        // 예:
        // 오늘이 2026-05-07이면
        // today에는 2026-05-07이 들어간다.
        LocalDate today = LocalDate.now();

        // 이번 주 월요일 날짜를 구한다.
        //
        // today.with(DayOfWeek.MONDAY)
        // → today가 포함된 주의 월요일 날짜로 변경한다.
        //
        // 예:
        // today = 2026-05-07 목요일
        // monday = 2026-05-04 월요일
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        // 이번 주 일요일 날짜를 구한다.
        //
        // today.with(DayOfWeek.SUNDAY)
        // → today가 포함된 주의 일요일 날짜로 변경한다.
        //
        // 예:
        // today = 2026-05-07 목요일
        // sunday = 2026-05-10 일요일
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);

        // 사용자가 담당자로 배정된 청소 업무가
        // 이번 주 일정에 몇 개 등록되어 있는지 조회한다.
        //
        // user.getId()
        // → 조회할 사용자의 고유 번호
        //
        // monday
        // → 이번 주 월요일
        //
        // sunday
        // → 이번 주 일요일
        //
        // 즉, 월요일부터 일요일까지의 기간 동안
        // 해당 사용자가 맡은 청소 일정 개수를 계산한다.
        long weeklyCleanCount = scheduleRepository.countSchedulesByAssignedWorkAndWeek(
                user.getId(),
                monday,
                sunday
        );

        // 이번 주 남은 청소 횟수를 계산한다.
        //
        // 여기서는 기준 청소 횟수를 2번으로 보고 있다.
        //
        // 2 - weeklyCleanCount
        // → 이번 주에 아직 남은 청소 횟수
        //
        // Math.max(0, ...)
        // → 계산 결과가 음수가 되지 않도록 막는다.
        //
        // 예:
        // weeklyCleanCount = 1이면 remainingCleanCount = 1
        // weeklyCleanCount = 2이면 remainingCleanCount = 0
        // weeklyCleanCount = 3이면 2 - 3 = -1이지만,
        // Math.max 때문에 remainingCleanCount = 0
        long remainingCleanCount = Math.max(0, 2 - weeklyCleanCount);

        // 사용자의 전체 벌금 금액을 조회한다.
        //
        // 정산 여부와 상관없이
        // 해당 사용자에게 발생한 벌금 금액을 모두 더한 값이다.
        int totalPenaltyAmount = penaltyRepository.sumAmountByUserId(user.getId());

        // 사용자의 미정산 벌금 금액을 조회한다.
        //
        // adjustmentYn 값이 'N'인 벌금만 합산한다.
        //
        // 즉, 아직 정산되지 않은 벌금 총액이다.
        int unpaidPenaltyAmount = penaltyRepository.sumUnpaidAmountByUserId(user.getId());

        // 위에서 조회하고 계산한 값들을
        // 마이페이지 응답용 DTO에 담아서 반환한다.
        return MyPageStatsResponseDto.builder()

                // 사용자 고유 번호를 DTO에 넣는다.
                .userSeq(user.getId())

                // 사용자 이름을 DTO에 넣는다.
                .name(user.getName())

                // 사용자 권한을 문자열로 변환해서 DTO에 넣는다.
                //
                // user.getRole()
                // → User의 권한 enum 값을 가져온다.
                //
                // name()
                // → enum 값을 문자열로 변환한다.
                //
                // 예:
                // Role.ADMIN → "ADMIN"
                // Role.USER → "USER"
                .role(user.getRole().name())

                // 이번 주 청소 횟수를 DTO에 넣는다.
                .weeklyCleanCount(weeklyCleanCount)

                // 이번 주 남은 청소 횟수를 DTO에 넣는다.
                .remainingCleanCount(remainingCleanCount)

                // 전체 벌금 금액을 DTO에 넣는다.
                .totalPenaltyAmount(totalPenaltyAmount)

                // 미정산 벌금 금액을 DTO에 넣는다.
                .unpaidPenaltyAmount(unpaidPenaltyAmount)

                // 위에서 설정한 값들로 MyPageStatsResponseDto 객체를 최종 생성한다.
                .build();
    }
}