package com.cleaning.freshup.domain.penalty.controller;

import com.cleaning.freshup.domain.penalty.dto.PenaltyResponseDto;
import com.cleaning.freshup.domain.penalty.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.cleaning.freshup.domain.penalty.dto.PenaltyRequestDto;

import java.util.List;

@RestController
@RequestMapping("/api/penalties")
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyService penaltyService;

    @GetMapping
    public List<PenaltyResponseDto> getPenalties() {
        return penaltyService.getPenalties();
    }

    @PutMapping("/{penaltyId}")
    public PenaltyResponseDto updatePenalty(
            @PathVariable Long penaltyId,
            @RequestBody PenaltyRequestDto requestDto
    ) {
        return penaltyService.updatePenalty(penaltyId, requestDto);
    }
}