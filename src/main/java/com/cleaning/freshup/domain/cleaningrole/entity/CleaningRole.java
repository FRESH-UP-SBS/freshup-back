package com.cleaning.freshup.domain.cleaningrole.entity;

import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.work.entity.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "TB_CLEANING_ROLE")
public class CleaningRole {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cleaning_role_seq")
    @SequenceGenerator(
            name = "cleaning_role_seq",
            sequenceName = "SEQ_CLEANING_ROLE",
            allocationSize = 1
    )
    @Column(name = "CLEANING_ROLE_SEQ")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_SEQ", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_SEQ", nullable = false)
    private Work work;

    public CleaningRole(User user, Work work) {
        this.user = user;
        this.work = work;
    }
}