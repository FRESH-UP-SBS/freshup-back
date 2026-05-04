package com.cleaning.freshup.domain.user.repository;

import com.cleaning.freshup.domain.user.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    Optional<SocialAccount> findByProviderAndProviderUserId(String provider, String providerUserId);

    Optional<SocialAccount> findByProviderUserId(String providerUserId);
}