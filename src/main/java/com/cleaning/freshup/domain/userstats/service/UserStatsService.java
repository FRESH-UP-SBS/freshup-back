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

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final PenaltyRepository penaltyRepository;

    public MyPageStatsResponseDto getMyPageStats(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        LocalDate today = LocalDate.now();

        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);

        long weeklyCleanCount = scheduleRepository.countSchedulesByAssignedWorkAndWeek(
                user.getId(),
                monday,
                sunday
        );

        long remainingCleanCount = Math.max(0, 2 - weeklyCleanCount);

        int totalPenaltyAmount = penaltyRepository.sumAmountByUserId(user.getId());
        int unpaidPenaltyAmount = penaltyRepository.sumUnpaidAmountByUserId(user.getId());

        return MyPageStatsResponseDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .role(user.getRole().name())
                .weeklyCleanCount(weeklyCleanCount)
                .remainingCleanCount(remainingCleanCount)
                .totalPenaltyAmount(totalPenaltyAmount)
                .unpaidPenaltyAmount(unpaidPenaltyAmount)
                .build();
    }
}