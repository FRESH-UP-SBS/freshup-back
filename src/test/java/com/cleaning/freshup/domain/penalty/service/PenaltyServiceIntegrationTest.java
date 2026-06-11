package com.cleaning.freshup.domain.penalty.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cleaning.freshup.domain.penalty.entity.Penalty;
import com.cleaning.freshup.domain.penalty.repository.PenaltyRepository;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class PenaltyServiceIntegrationTest {
    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private PenaltyRepository penaltyRepository;

    @Test
    void 벌금이_실제로_DB에_저장된다() {
        penaltyService.applyPenaltyForInsufficientCleaning();

        List<Penalty> result = penaltyRepository.findAll();
        assertThat(result).isNotEmpty();
    }

}
