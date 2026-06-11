package com.cleaning.freshup.domain.penalty.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class PenaltyApplyDto {
    private final Long userSeq;
    private final BigDecimal amount;
    private final LocalDate weekStart;
    private final LocalDate weekEnd;
    private final int completedCount;
    private final int missCount;
}