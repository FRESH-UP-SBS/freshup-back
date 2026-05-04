package com.cleaning.freshup.domain.schedule.controller;

import com.cleaning.freshup.domain.schedule.dto.ScheduleRequestDto;
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

    @GetMapping("/test")
    public String test() {
        return "schedule api ok";
    }

    @GetMapping
    public List<ScheduleResponseDto> getSchedules(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return scheduleService.getSchedules(year, month);
    }

    @PostMapping
    public ScheduleResponseDto createSchedule(
            @RequestBody ScheduleRequestDto requestDto
    ) {
        return scheduleService.createSchedule(requestDto);
    }

    @PutMapping("/{scheduleId}")
    public ScheduleResponseDto updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleRequestDto requestDto
    ) {
        return scheduleService.updateSchedule(scheduleId, requestDto);
    }

    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(
            @PathVariable Long scheduleId
    ) {
        scheduleService.deleteSchedule(scheduleId);
    }
}