package com.cleaning.freshup.scheduler;

import com.cleaning.freshup.domain.penalty.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 매주 월요일 새벽 6시에 실행되어
 * 이번 주(전 주) 청소를 2회 미완료한 회원에게 벌금을 부과하는 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PenaltyScheduler {

    private final PenaltyService penaltyService;

    /**
     * 매주 월요일 06:00에 실행
     * cron 표현식: 초 분 시 일 월 요일
     * 0 0 6 * * MON → 매주 월요일 오전 6시 0분 0초
     */
    @Scheduled(cron = "0 0 6 * * MON", zone = "Asia/Seoul")
    public void processWeeklyCleaningPenalty() {
        log.info("[PenaltyScheduler] ===== 주간 청소 미이행 벌금 처리 시작 =====");
        try {
            penaltyService.applyPenaltyForInsufficientCleaning();
            log.info("[PenaltyScheduler] ===== 주간 청소 미이행 벌금 처리 완료 =====");
        } catch (Exception e) {
            log.error("[PenaltyScheduler] 벌금 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
