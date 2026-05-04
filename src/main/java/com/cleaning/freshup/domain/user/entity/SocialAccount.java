package com.cleaning.freshup.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_social_account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "social_account_seq")
    @SequenceGenerator(name = "social_account_seq", sequenceName = "SOCIAL_ACCOUNT_SEQ", allocationSize = 1)
    @Column(name = "social_account_seq")
    private Long social_account_seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq")
    private User user;

    @Column(name = "provider")
    private String provider; // kakao

    @Column(name = "provider_user_id")
    private String providerUserId; // 카카오 id

    @Builder
    public SocialAccount(String provider, String providerUserId, User user) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.user = user;
    }
}