package com.cleaning.freshup.domain.schedule.service;

import com.cleaning.freshup.domain.schedule.dto.ScheduleResponseDto;
import com.cleaning.freshup.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<ScheduleResponseDto> getSchedules(int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return scheduleRepository.findSchedulesByMonth(start, end)
                .stream()
                .map(ScheduleResponseDto::from)
                .toList();
    }
}