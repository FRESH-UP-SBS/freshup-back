package com.cleaning.freshup.domain.penalty.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PenaltyAddRequestDto {
    private Long userId;
    private Integer amount;
}
