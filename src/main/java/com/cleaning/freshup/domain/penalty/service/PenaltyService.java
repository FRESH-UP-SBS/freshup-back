package com.cleaning.freshup.domain.penalty.service;

import com.cleaning.freshup.domain.penalty.dto.PenaltyResponseDto;
import com.cleaning.freshup.domain.penalty.entity.Penalty;
import com.cleaning.freshup.domain.penalty.repository.PenaltyRepository;
import com.cleaning.freshup.domain.schedule.repository.ScheduleRepository;
import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;
import com.cleaning.freshup.domain.userstats.entity.UserStats;
import com.cleaning.freshup.domain.userstats.repository.UserStatsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import com.cleaning.freshup.domain.penalty.dto.PenaltyApplyDto;
import com.cleaning.freshup.domain.penalty.dto.PenaltyRequestDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

// 이 클래스가 Service 역할을 하는 클래스라는 뜻이다.
//
// Service는 Controller와 Repository 사이에서
// 실제 비즈니스 로직을 처리하는 계층이다.
//
// 예:
// Controller → 요청 받기
// Service → 실제 로직 처리
// Repository → DB 접근
@Slf4j
@Service

// Lombok 어노테이션
// final이 붙은 필드를 매개변수로 받는 생성자를 자동으로 만들어준다.
//
// 여기서는 penaltyRepository를 주입받는 생성자가 자동 생성된다.
// 즉, new PenaltyRepository()를 직접 하지 않아도
// Spring이 알아서 객체를 넣어준다.
@RequiredArgsConstructor
public class PenaltyService {

    // 벌금 데이터를 DB에서 조회하거나 수정할 때 사용하는 Repository이다.
    //
    // Service는 직접 SQL을 실행하지 않고,
    // Repository를 통해 DB 작업을 요청한다.
    private final PenaltyRepository penaltyRepository;

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserStatsRepository userStatsRepository;

    /** 주당 청소 최소 완료 횟수 */
    private static final int REQUIRED_CLEAN_COUNT_PER_WEEK = 2;

    /** 1회 미이행 시 부과 벌금액 (원) */
    @Value("${freshup.penalty.amount-per-miss:3000}")
    private static BigDecimal penaltyAmountPerMiss = BigDecimal.valueOf(3000);

    /**
     * 전 주(월~일) 청소 횟수가 2회 미만인 회원에게 벌금을 부과합니다.
     * - 스케줄러에서 매주 월요일 06:00에 호출됩니다.
     */

    // 벌금 목록을 조회하는 메서드이다.
    //
    // Controller에서 GET /api/penalties 요청이 들어오면
    // 이 메서드가 호출된다.
    public Page<PenaltyResponseDto> getPenalties(List<Long> assignees, String paymentStatus, String startDate,
            String endDate,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        // penaltyRepository.findAllWithUser()
        // → DB에서 모든 벌금 정보를 조회한다.
        // → 이때 벌금과 연결된 사용자 정보도 함께 조회한다.
        return penaltyRepository.findAllWithFilter(
                assignees,
                paymentStatus,
                startDate != null ? LocalDate.parse(startDate) : null,
                endDate != null ? LocalDate.parse(endDate) : null,
                pageable).map(PenaltyResponseDto::from);
    }

    // 벌금 정보를 수정하는 메서드이다.
    //
    // @Transactional은 이 메서드 안에서 일어나는 DB 작업을
    // 하나의 트랜잭션으로 묶어준다.
    //
    // 트랜잭션이란?
    // DB 작업을 하나의 단위로 처리하는 것이다.
    //
    // 이 메서드에서는 Penalty Entity의 값을 변경하기 때문에
    // @Transactional이 필요하다.
    @Transactional
    public PenaltyResponseDto updatePenalty(Long penaltyId, PenaltyRequestDto requestDto) {

        // penaltyId에 해당하는 벌금 정보를 DB에서 조회한다.
        //
        // findById(penaltyId)
        // → penaltyId와 같은 id를 가진 Penalty를 찾는다.
        //
        // orElseThrow(...)
        // → 해당 벌금 정보가 없으면 예외를 발생시킨다.
        //
        // 예:
        // penaltyId가 1이면 id가 1인 벌금 정보를 찾는다.
        // 없으면 "존재하지 않는 벌금 정보입니다." 오류가 발생한다.
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 벌금 정보입니다."));

        // 요청으로 받은 정산 여부 값을 Penalty Entity에 반영한다.
        //
        // requestDto.getAdjustmentYn()
        // → 클라이언트가 보낸 adjustmentYn 값을 가져온다.
        //
        // penalty.updateAdjustmentYn(...)
        // → Penalty Entity의 adjustmentYn 값을 수정한다.
        //
        // 예:
        // requestDto.getAdjustmentYn() 값이 "Y"이면
        // 해당 벌금은 정산 완료 상태로 변경된다.
        penalty.updateAdjustmentYn(requestDto.getAdjustmentYn());

        // 수정된 Penalty Entity를 응답용 DTO로 변환해서 반환한다.
        //
        // @Transactional 상태에서 Entity 값을 변경하면
        // JPA가 변경된 내용을 감지해서 DB에 반영한다.
        //
        // 즉, 여기서는 penaltyRepository.save(penalty)를 직접 호출하지 않아도
        // 트랜잭션이 끝날 때 수정 내용이 저장될 수 있다.
        return PenaltyResponseDto.from(penalty);
    }

    /**
     * 전 주(월~일) 청소 횟수가 2회 미만인 회원에게 벌금을 부과합니다.
     * - 스케줄러에서 매주 월요일 06:00에 호출됩니다.
     */
    @Transactional
    public void applyPenaltyForInsufficientCleaning() {
        // ① 전 주 기간 계산 (월요일 00:00 ~ 일요일 23:59:59)
        LocalDate today = LocalDate.now(); // 현재 월요일
        LocalDate lastMonday = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastSunday = lastMonday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 변경 후
        LocalDate weekStart = lastMonday;
        LocalDate weekEnd = lastSunday;

        log.info("[PenaltyService] 검사 기간: {} ~ {}", weekStart, weekEnd);

        // ② 전체 활성 회원 조회
        List<User> allUsers = userRepository.findAll();
        log.info("[PenaltyService] 대상 회원 수: {}", allUsers.size());

        int penaltyCount = 0;

        for (User user : allUsers) {
            // ③ 해당 주에 완료한 청소 일정 횟수 조회
            int completedCount = scheduleRepository.countCompletedScheduleByUserAndPeriod(
                    user.getId(), weekStart, weekEnd);

            int missCount = REQUIRED_CLEAN_COUNT_PER_WEEK - completedCount;

            if (missCount <= 0) {
                log.debug("[PenaltyService] 회원 {} - 청소 완료({}/{}), 벌금 없음",
                        user.getId(), completedCount, REQUIRED_CLEAN_COUNT_PER_WEEK);
                continue;
            }

            // ④ 벌금 부과
            BigDecimal totalPenalty = penaltyAmountPerMiss.multiply(BigDecimal.valueOf(missCount));

            PenaltyApplyDto applyDto = PenaltyApplyDto.builder()
                    .userSeq(user.getId())
                    .amount(totalPenalty)
                    .weekStart(weekStart)
                    .weekEnd(weekEnd)
                    .completedCount(completedCount)
                    .missCount(missCount)
                    .build();

            savePenalty(applyDto, user);
            updateUserStats(user.getId(), totalPenalty);
            penaltyCount++;

            log.info("[PenaltyService] 벌금 부과 - 회원 seq={}, 청소 완료 {}/{}회, 미이행 {}회, 벌금 {}원",
                    user.getId(), completedCount, REQUIRED_CLEAN_COUNT_PER_WEEK,
                    missCount, totalPenalty);
        }

        log.info("[PenaltyService] 벌금 처리 완료 - 총 {}명 부과", penaltyCount);
    }

    /** TB_PENALTY 에 벌금 레코드 저장 */
    private void savePenalty(PenaltyApplyDto dto, User user) {
        Penalty penalty = new Penalty(user, dto.getAmount().intValue(), "N", LocalDate.now(), LocalDate.now());

        penaltyRepository.save(penalty);
    }

    /** TB_USER_STATS 의 총벌금금액(TOTAL_PENALTY_AMOUNT) 누적 업데이트 */
    private void updateUserStats(Long userSeq, BigDecimal addedAmount) {
        // 만약 TB_USER_STATS에 해당 회원의 정보가 없다면 UserStats 엔티티를 새로 만든다.
        UserStats stats = userStatsRepository.findById(userSeq)
                .orElseGet(() -> {
                    UserStats newStats = UserStats.builder()
                            .userSeq(userSeq)
                            .remainingCleanCount(0)
                            .totalPenaltyAmount(BigDecimal.ZERO)
                            .createdDate(LocalDateTime.now())
                            .updatedDate(LocalDateTime.now())
                            .build();
                    return userStatsRepository.save(newStats);
                });

        stats.addPenaltyAmount(addedAmount);
        userStatsRepository.save(stats);
    }

    // 벌금 정보를 삭제하는 메서드
    public void deletePenalty(Long penaltyId) {
        penaltyRepository.deleteById(penaltyId);
    }
}