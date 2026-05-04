package com.cleaning.freshup.domain.schedule.dto;

import com.cleaning.freshup.domain.schedule.entity.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ScheduleResponseDto {

    private Long id;
    private LocalDate date;
    private String taskName;
    private String memberName;

    public static ScheduleResponseDto from(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .date(schedule.getDate())
                .taskName(schedule.getWork().getWorkName())
                .memberName(schedule.getUser().getName())
                .build();
    }
}