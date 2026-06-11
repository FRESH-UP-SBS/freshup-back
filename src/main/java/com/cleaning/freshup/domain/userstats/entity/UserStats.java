package com.cleaning.freshup.domain.userstats.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_USER_STATS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStats {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_stats_seq_gen")
    @SequenceGenerator(name = "user_stats_seq_gen", sequenceName = "USER_STATS_SEQ", allocationSize = 1)
    @Column(name = "USER_STATS_SEQ")
    private Long userStatsSeq;

    @Column(name = "USER_SEQ", nullable = false)
    private Long userSeq;

    @Column(name = "REMAINING_CLEAN_COUNT", nullable = false, columnDefinition = "NUMBER DEFAULT 0")
    private Integer remainingCleanCount = 0;

    @Column(name = "TOTAL_PENALTY_AMOUNT", nullable = false, columnDefinition = "NUMBER DEFAULT 0")
    private BigDecimal totalPenaltyAmount = BigDecimal.ZERO;

    @Column(name = "CREATED_DATE", nullable = false, columnDefinition = "DATE DEFAULT SYSDATE")
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE", columnDefinition = "DATE DEFAULT SYSDATE")
    private LocalDateTime updatedDate;

    /** 누적 벌금 금액 추가 */
    public void addPenaltyAmount(BigDecimal amount) {
        this.totalPenaltyAmount = this.totalPenaltyAmount.add(amount);
        this.updatedDate = LocalDateTime.now();
    }
}
