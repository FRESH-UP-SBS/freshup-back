package com.cleaning.freshup.domain.schedule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ScheduleRequestDto {

    private Long userId;
    private Long workId;
    private LocalDate date;
}