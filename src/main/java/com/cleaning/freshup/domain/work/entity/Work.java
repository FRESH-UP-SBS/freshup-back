package com.cleaning.freshup.domain.work.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "TB_WORK")
public class Work {

    @Id
    @Column(name = "WORK_SEQ")
    private Long id;

    @Column(name = "WORK_NAME")
    private String workName;
}