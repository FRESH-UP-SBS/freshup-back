package com.cleaning.freshup.auth.handler;

import com.cleaning.freshup.auth.jwt.JwtTokenProvider;
import com.cleaning.freshup.auth.jwt.JwtTokenService;

import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String providerId = oAuth2User.getAttribute("id").toString();

        // 1. 두 종류의 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(providerId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(providerId);

        // 2. Access Token 쿠키 (짧은 수명)
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600); // 1시간
        response.addCookie(accessCookie);

        // 3. Refresh Token 쿠키 (긴 수명)
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/api/auth/reissue"); // `/api/auth/reissue`로 이동할 경우에만 (재발급 요청시에만) 서버로 전송되도록 제한(보안 강화)
        refreshCookie.setMaxAge(60 * 60 * 24 * 7); // 7일
        response.addCookie(refreshCookie);

        // 4. Refresh Token은 DB에도 저장해야 함 (검증용)
        tokenService.saveRefreshToken(providerId, refreshToken);

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/calendar");
    }
}