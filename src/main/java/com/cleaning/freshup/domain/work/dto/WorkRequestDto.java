package com.cleaning.freshup.domain.work.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class WorkRequestDto {

    private String workName;
    private List<Long> memberIds;
}