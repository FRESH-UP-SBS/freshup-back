package com.cleaning.freshup.domain.schedule.controller;

import com.cleaning.freshup.domain.schedule.dto.ScheduleResponseDto;
import com.cleaning.freshup.domain.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public List<ScheduleResponseDto> getSchedules(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return scheduleService.getSchedules(year, month);
    }
}