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

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // String providerId = String.valueOf(oAuth2User.getAttribute("id"));
        Long id = oAuth2User.getAttribute("id");
        String providerId = id.toString();
        String token = jwtTokenProvider.generateToken(providerId);

        // 쿠키 생성
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true); // JS에서 접근 불가 (XSS 방어)
        cookie.setSecure(false); // HTTPS에서만 전송
        cookie.setPath("/"); // 모든 경로에서 유효
        cookie.setMaxAge(3600); // 유효 시간 (1시간)
        response.addCookie(cookie);

        // 프론트엔드로 토큰을 쿼리파라미터로 전달
        getRedirectStrategy().sendRedirect(request, response,
                "http://localhost:3000");
    }
}