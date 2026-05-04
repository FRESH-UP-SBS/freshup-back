package com.cleaning.freshup.auth.jwt;

import com.cleaning.freshup.domain.user.entity.SocialAccount;
import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.SocialAccountRepository;
import com.cleaning.freshup.domain.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveRefreshToken(String providerId, String refreshToken) {
        // providerId로 유저를 찾아서 토큰 갱신
        SocialAccount socialAccount = socialAccountRepository.findByProviderUserId(providerId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        User user = socialAccount.getUser();

        user.updateRefreshToken(refreshToken);

        // DB에 저장
        userRepository.save(user);
    }

}
