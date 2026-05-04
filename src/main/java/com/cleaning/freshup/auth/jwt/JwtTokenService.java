package com.cleaning.freshup.auth.jwt;

import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final UserRepository userRepository;

    /**
     * 이메일을 기반으로 사용자를 찾아 리프레시 토큰을 저장/갱신합니다.
     */
    @Transactional
    public void saveRefreshToken(String email, String refreshToken) {
        // 1. 이메일로 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        // 2. 리프레시 토큰 갱신
        user.updateRefreshToken(refreshToken);

        // 3. 변경 감지(Dirty Checking) 덕분에 사실 save()를 호출하지 않아도 되지만,
        // 명시적인 코드를 위해 유지하거나 생략 가능합니다.
        userRepository.save(user);
    }
}