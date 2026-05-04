package com.cleaning.freshup.domain.work.dto;

import com.cleaning.freshup.domain.work.entity.Work;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WorkResponseDto {

    private Long id;
    private String workName;
    private List<Long> memberIds;
    private List<String> memberNames;

    public static WorkResponseDto from(Work work) {
        return WorkResponseDto.builder()
                .id(work.getId())
                .workName(work.getWorkName())
                .build();
    }
}