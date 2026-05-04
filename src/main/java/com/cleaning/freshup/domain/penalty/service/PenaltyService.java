package com.cleaning.freshup.domain.penalty.service;

import com.cleaning.freshup.domain.penalty.dto.PenaltyResponseDto;
import com.cleaning.freshup.domain.penalty.entity.Penalty;
import com.cleaning.freshup.domain.penalty.repository.PenaltyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.cleaning.freshup.domain.penalty.dto.PenaltyRequestDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;

    public List<PenaltyResponseDto> getPenalties() {
        return penaltyRepository.findAllWithUser()
                .stream()
                .map(PenaltyResponseDto::from)
                .toList();
    }

    @Transactional
    public PenaltyResponseDto updatePenalty(Long penaltyId, PenaltyRequestDto requestDto) {
        Penalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 벌금 정보입니다."));

        penalty.updateAdjustmentYn(requestDto.getAdjustmentYn());

        return PenaltyResponseDto.from(penalty);
    }
}