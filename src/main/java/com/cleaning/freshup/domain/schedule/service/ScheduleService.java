package com.cleaning.freshup.domain.schedule.service;

import com.cleaning.freshup.domain.schedule.dto.ScheduleRequestDto;
import com.cleaning.freshup.domain.schedule.dto.ScheduleResponseDto;
import com.cleaning.freshup.domain.schedule.entity.Schedule;
import com.cleaning.freshup.domain.schedule.repository.ScheduleRepository;
import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;
import com.cleaning.freshup.domain.work.entity.Work;
import com.cleaning.freshup.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final WorkRepository workRepository;

    public List<ScheduleResponseDto> getSchedules(int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return scheduleRepository.findSchedulesByMonth(start, end)
                .stream()
                .map(ScheduleResponseDto::from)
                .toList();
    }

    @Transactional
    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto) {

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Work work = workRepository.findById(requestDto.getWorkId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 업무입니다."));

        Schedule schedule = new Schedule(
                user,
                work,
                requestDto.getDate()
        );

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return ScheduleResponseDto.from(savedSchedule);
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(Long scheduleId, ScheduleRequestDto requestDto) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Work work = workRepository.findById(requestDto.getWorkId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 업무입니다."));

        schedule.update(
                user,
                work,
                requestDto.getDate()
        );

        return ScheduleResponseDto.from(schedule);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        scheduleRepository.delete(schedule);
    }
}