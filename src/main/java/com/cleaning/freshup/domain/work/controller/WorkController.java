package com.cleaning.freshup.domain.work.controller;

import com.cleaning.freshup.domain.work.dto.WorkRequestDto;
import com.cleaning.freshup.domain.work.dto.WorkResponseDto;
import com.cleaning.freshup.domain.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @GetMapping
    public List<WorkResponseDto> getWorks() {
        return workService.getWorks();
    }

    @PostMapping
    public WorkResponseDto createWork(@RequestBody WorkRequestDto requestDto) {
        return workService.createWork(requestDto);
    }

    @PutMapping("/{workId}")
    public WorkResponseDto updateWork(
            @PathVariable Long workId,
            @RequestBody WorkRequestDto requestDto
    ) {
        return workService.updateWork(workId, requestDto);
    }

    @DeleteMapping("/{workId}")
    public void deleteWork(@PathVariable Long workId) {
        workService.deleteWork(workId);
    }
}