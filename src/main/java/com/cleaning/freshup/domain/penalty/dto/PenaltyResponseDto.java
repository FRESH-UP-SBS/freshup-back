package com.cleaning.freshup.domain.penalty.dto;

import com.cleaning.freshup.domain.penalty.entity.Penalty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PenaltyResponseDto {

    private Long id;
    private Long userId;
    private String name;
    private Integer amount;
    private String adjustmentYn;
    private String status;

    public static PenaltyResponseDto from(Penalty penalty) {
        String adjustmentYn = penalty.getAdjustmentYn();

        return PenaltyResponseDto.builder()
                .id(penalty.getId())
                .userId(penalty.getUser().getId())
                .name(penalty.getUser().getName())
                .amount(penalty.getAmount())
                .adjustmentYn(adjustmentYn)
                .status("Y".equals(adjustmentYn) ? "정산완료" : "정산필요")
                .build();
    }
}