package com.cleaning.freshup.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
    @Column(name = "USER_SEQ")
    private Long id;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private Role role;

    public enum Role {
        ADMIN, USER
    }

    public User updateName(String name) {
        this.name = name;
        return this;
    }

    // Refresh Token 필드 추가
    @Column(name = "refresh_token")
    private String refreshToken;

    public void updateRefreshToken(String newToken) {
        this.refreshToken = newToken;
    }

    // 로그아웃 시 토큰 제거
    public void destroyRefreshToken() {
        this.refreshToken = null;
    }

}