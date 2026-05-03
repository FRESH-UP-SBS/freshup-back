package com.cleaning.freshup.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq") // sequence 전략 사용
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1) // 시퀀스 설정, allocationSize는 1로
                                                                                         // 설정하여 시퀀스 번호가 1씩 증가하도록 함
    @Column(name = "id") //
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String provider; // "kakao"

    @Column(nullable = false)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role {
        ADMIN, USER
    }

    public User updateNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

}