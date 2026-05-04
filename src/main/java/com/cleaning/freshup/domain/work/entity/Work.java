package com.cleaning.freshup.domain.work.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "TB_WORK")
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_seq")
    @SequenceGenerator(
            name = "work_seq",
            sequenceName = "SEQ_WORK",
            allocationSize = 1
    )
    @Column(name = "WORK_SEQ")
    private Long id;

    @Column(name = "WORK_NAME", nullable = false)
    private String workName;

    @Column(name = "USE_YN")
    private String useYn = "Y";

    public Work(String workName) {
        this.workName = workName;
        this.useYn = "Y";
    }

    public void updateWorkName(String workName) {
        this.workName = workName;
    }

    public void deleteWork() {
        this.useYn = "N";
    }
}