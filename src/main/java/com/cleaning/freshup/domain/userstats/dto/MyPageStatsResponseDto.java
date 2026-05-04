package com.cleaning.freshup.domain.userstats.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageStatsResponseDto {

    private Long userId;
    private String name;
    private String role;

    private long weeklyCleanCount;
    private long remainingCleanCount;

    private int totalPenaltyAmount;
    private int unpaidPenaltyAmount;
}
