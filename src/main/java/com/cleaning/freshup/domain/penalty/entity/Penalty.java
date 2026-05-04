package com.cleaning.freshup.domain.penalty.entity;

import com.cleaning.freshup.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "TB_PENALTY")
public class Penalty {

    @Id
    @Column(name = "PENALTY_SEQ")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_SEQ", nullable = false)
    private User user;

    @Column(name = "AMOUNT", nullable = false)
    private Integer amount;

    @Column(name = "ADJUSTMENT_YN", nullable = false)
    private String adjustmentYn;

    public void updateAdjustmentYn(String adjustmentYn) {
        this.adjustmentYn = adjustmentYn;
    }
}