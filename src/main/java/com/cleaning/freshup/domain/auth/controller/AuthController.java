package com.cleaning.freshup.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cleaning.freshup.auth.jwt.JwtTokenProvider;
import com.cleaning.freshup.domain.user.entity.SocialAccount;
import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.SocialAccountRepository;
import com.cleaning.freshup.domain.user.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;

    // 리프레시 토큰으로 accessToken 재발급 엔드포인트
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        // 1. 쿠키에서 refreshToken 찾기
        // refreshToken으로 accessToken을 재발급한다.
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        // 2. 토큰 검증 (유효성 및 DB 대조)
        if (refreshToken != null && jwtTokenProvider.validate(refreshToken)) {
            String providerId = jwtTokenProvider.getProviderId(refreshToken); // token에 담긴 providerId 추출

            // 1) SocialAccount를 통해 providerId에 해당하는 사용자의 이메일이나 고유 식별자를 가져옵니다.
            SocialAccount socialAccount = socialAccountRepository.findByProviderUserId(providerId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            User user = socialAccount.getUser();

            // 2) DB에 저장된 refreshToken과 클라이언트가 보낸 토큰이 일치하는지 대조
            if (!refreshToken.equals(user.getRefreshToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
            }

            // 3) 새로운 AccessToken 생성
            String newAccessToken = jwtTokenProvider.generateAccessToken(providerId);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(providerId);

            // 4) 새 쿠키 설정 및 응답
            Cookie accessCookie = new Cookie("accessToken", newAccessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(3600); // 1시간
            response.addCookie(accessCookie);

            // 4) 새 쿠키 설정 및 응답
            Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/api/auth/reissue"); // `/api/auth/reissue`로 이동할 경우에만 (재발급 요청시에만) 서버로 전송되도록 제한(보안 강화)
            refreshCookie.setMaxAge(604800); // 7일
            response.addCookie(refreshCookie);

            // 5) DB에 저장된 refreshToken도 갱신
            user.updateRefreshToken(newRefreshToken);
            userRepository.save(user);

            return ResponseEntity.ok().body("Token reissued successfully");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
    }
}