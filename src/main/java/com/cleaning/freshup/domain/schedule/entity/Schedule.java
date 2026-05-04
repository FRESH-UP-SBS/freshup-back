package com.cleaning.freshup.domain.schedule.entity;

import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.work.entity.Work;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "TB_SCHEDULE")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schedule_seq")
    @SequenceGenerator(
            name = "schedule_seq",
            sequenceName = "SEQ_SCHEDULE",
            allocationSize = 1
    )
    @Column(name = "EVENT_SEQ")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_SEQ", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_SEQ", nullable = false)
    private Work work;

    @Column(name = "EVENT_DATE", nullable = false)
    private LocalDate date;
}