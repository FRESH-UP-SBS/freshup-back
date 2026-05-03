package com.cleaning.freshup.domain.schedule.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Column;
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
    private Long id;

    @Column(name = "SCHEDULE_DATE")
    private LocalDate date;

    @Column(name = "CONTENT")
    private String content;
}