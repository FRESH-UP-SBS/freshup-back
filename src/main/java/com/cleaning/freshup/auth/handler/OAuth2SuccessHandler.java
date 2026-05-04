package com.cleaning.freshup.auth.handler;

import com.cleaning.freshup.auth.jwt.JwtTokenProvider;
import com.cleaning.freshup.auth.jwt.JwtTokenService;
import com.cleaning.freshup.domain.user.entity.User;

import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.cleaning.freshup.domain.user.repository.UserRepository;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenService tokenService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // OAuth2 제공자마다 이메일 필드명이 다를 수 있으니 주의 (보통 "email")
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            // 카카오 등 특정 플랫폼은 attributes 내의 kakao_account 내부에 있을 수 있음
            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            if (kakaoAccount != null)
                email = (String) kakaoAccount.get("email");
        }

        // email뿐만 아니라 DB에서 조회한 user의 seq도 같이 넘겨줘야 합니다.
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 1. Email 기반 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(email, user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(email, user.getId());

        // 2. Access Token 쿠키 설정 (기존과 동일)
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600);
        response.addCookie(accessCookie);

        // 3. Refresh Token 쿠키 설정
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/api/auth/reissue");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(refreshCookie);

        // 4. DB 저장 시에도 email을 키로 사용 (JwtTokenService 수정 필요할 수 있음)
        tokenService.saveRefreshToken(email, refreshToken);

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/calendar");
    }
}