package com.cleaning.freshup.domain.penalty.service;

import com.cleaning.freshup.domain.penalty.repository.PenaltyRepository;
import com.cleaning.freshup.domain.schedule.repository.ScheduleRepository;
import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;
import com.cleaning.freshup.domain.userstats.entity.UserStats;
import com.cleaning.freshup.domain.userstats.repository.UserStatsRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PenaltyServiceTest {

        @InjectMocks
        private PenaltyService penaltyService;

        @Mock
        private UserRepository UserRepository;
        @Mock
        private ScheduleRepository scheduleRepository;
        @Mock
        private PenaltyRepository penaltyRepository;
        @Mock
        private UserStatsRepository userStatsRepository;

        @BeforeEach
        void setUp() {
                ReflectionTestUtils.setField(penaltyService, "penaltyAmountPerMiss", new BigDecimal("3000"));
        }

        /** 테스트용 회원 생성 헬퍼 */
        private User makeUser(Long seq) {
                return User.builder().id(seq).name("테스트회원" + seq).build();
        }

        /** 테스트용 통계 생성 헬퍼 */
        private UserStats makeStats(Long userSeq) {
                return UserStats.builder()
                                .userSeq(userSeq)
                                .totalPenaltyAmount(BigDecimal.ZERO)
                                .remainingCleanCount(0)
                                .build();
        }

        @Test
        @DisplayName("청소를 1회만 한 회원 → 벌금 1회 부과")
        void shouldApplyOnePenalty_whenCleanedOnce() {
                // given
                User user = makeUser(1L);
                given(UserRepository.findAll()).willReturn(List.of(user));
                given(scheduleRepository.countCompletedScheduleByUserAndPeriod(
                                eq(1L), any(LocalDate.class), any(LocalDate.class)))
                                .willReturn(1); // 청소 1회 완료 (1회 미이행)
                given(userStatsRepository.findById(1L))
                                .willReturn(Optional.of(makeStats(1L)));

                // when
                penaltyService.applyPenaltyForInsufficientCleaning();

                // then
                verify(penaltyRepository, times(1)).save(
                                argThat(p -> p.getAmount().compareTo(Integer.valueOf("3000")) == 0));
                verify(userStatsRepository, times(1)).save(
                                argThat(s -> s.getTotalPenaltyAmount().compareTo(new BigDecimal("3000")) == 0));
        }

        @Test
        @DisplayName("청소를 0회 한 회원 → 벌금 2회 부과 (10000원)")
        void shouldApplyTwoPenalties_whenCleanedZeroTimes() {
                // given
                User user = makeUser(2L);
                given(UserRepository.findAll()).willReturn(List.of(user));
                given(scheduleRepository.countCompletedScheduleByUserAndPeriod(
                                eq(2L), any(LocalDate.class), any(LocalDate.class)))
                                .willReturn(0); // 청소 0회 완료 (2회 미이행)
                given(userStatsRepository.findById(2L))
                                .willReturn(Optional.of(makeStats(2L)));

                // when
                penaltyService.applyPenaltyForInsufficientCleaning();

                // then
                verify(penaltyRepository, times(1)).save(
                                argThat(p -> p.getAmount().compareTo(Integer.valueOf("6000")) == 0));
        }

        @Test
        @DisplayName("청소를 2회 완료한 회원 → 벌금 없음")
        void shouldNotApplyPenalty_whenCleanedTwice() {
                // given
                User user = makeUser(3L);
                given(UserRepository.findAll()).willReturn(List.of(user));
                given(scheduleRepository.countCompletedScheduleByUserAndPeriod(
                                eq(3L), any(LocalDate.class), any(LocalDate.class)))
                                .willReturn(2); // 정상 완료

                // when
                penaltyService.applyPenaltyForInsufficientCleaning();

                // then
                verify(penaltyRepository, never()).save(any());
                verify(userStatsRepository, never()).save(any());
        }

        @Test
        @DisplayName("여러 회원 혼재 → 미이행 회원만 벌금 부과")
        void shouldApplyPenaltyOnlyToNonCompliantUsers() {
                // given
                User userA = makeUser(10L); // 2회 완료
                User userB = makeUser(11L); // 1회 완료 → 벌금
                User userC = makeUser(12L); // 0회 완료 → 벌금

                given(UserRepository.findAll()).willReturn(List.of(userA, userB, userC));

                given(scheduleRepository.countCompletedScheduleByUserAndPeriod(
                                eq(10L), any(), any())).willReturn(2);
                given(scheduleRepository.countCompletedScheduleByUserAndPeriod(
                                eq(11L), any(), any())).willReturn(1);
                given(scheduleRepository.countCompletedScheduleByUserAndPeriod(
                                eq(12L), any(), any())).willReturn(0);

                given(userStatsRepository.findById(11L))
                                .willReturn(Optional.of(makeStats(11L)));
                given(userStatsRepository.findById(12L))
                                .willReturn(Optional.of(makeStats(12L)));

                // when
                penaltyService.applyPenaltyForInsufficientCleaning();

                // then: userA는 저장 없음, userB·userC는 각각 1회 저장
                verify(penaltyRepository, times(2)).save(any());
                verify(userStatsRepository, times(2)).save(any());
        }
}