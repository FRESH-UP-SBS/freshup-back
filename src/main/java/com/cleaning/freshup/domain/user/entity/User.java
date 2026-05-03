package com.cleaning.freshup.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq") // sequence 전략 사용
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1) // 시퀀스 생성 설정
    @Column(name = "user_seq")
    private Long user_seq;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role {
        ADMIN, USER
    }

    public User updateName(String name) {
        this.name = name;
        return this;
    }

}