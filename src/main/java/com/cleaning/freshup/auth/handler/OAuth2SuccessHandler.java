package com.cleaning.freshup.auth.handler;

import com.cleaning.freshup.auth.jwt.JwtTokenProvider;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        // String token = jwtTokenProvider.generateToken(authentication.getName());

        // // 1. 쿠키 생성
        // ResponseCookie cookie = ResponseCookie.from("accessToken", token)
        // .httpOnly(true) // JS에서 접근 불가 (XSS 방어)
        // .secure(true) // HTTPS에서만 전송
        // .path("/") // 모든 경로에서 유효
        // .maxAge(3600) // 유효 시간 (1시간)
        // .sameSite("Lax") // CSRF 방어 정책
        // .build();

        // // 2. 응답 헤더에 추가
        // response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // // 3. 프론트엔드로 리다이렉트
        // response.sendRedirect("http://localhost:3000/main");

        // 클로드 답변 -> 토큰 생성 -> 쿠키에 담아서 보내야됨 (수정 필요 !!!)
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String providerId = String.valueOf(oAuth2User.getAttribute("id"));
        String token = jwtTokenProvider.generateToken(providerId);

        // 프론트엔드로 토큰을 쿼리파라미터로 전달
        getRedirectStrategy().sendRedirect(request, response,
                "http://localhost:3000/auth/callback?token=" + token);
    }
}