package com.cleaning.freshup.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cleaning.freshup.auth.jwt.JwtTokenProvider;
import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken != null && jwtTokenProvider.validate(refreshToken)) {
            // 1) 토큰에서 email 추출
            String email = jwtTokenProvider.getEmail(refreshToken);

            // 2) 이메일로 사용자 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 3) DB의 리프레시 토큰과 비교
            if (!refreshToken.equals(user.getRefreshToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
            }

            // 4) 새로운 토큰 생성 (Email 기반)
            String newAccessToken = jwtTokenProvider.generateAccessToken(email, user.getId());
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(email, user.getId());

            // 4-1) Access Token 쿠키 설정 (기존과 동일)
            Cookie accessCookie = new Cookie("accessToken", newAccessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(3600);
            response.addCookie(accessCookie);

            // 4-2) Refresh Token 쿠키 설정
            Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/api/auth/reissue");
            refreshCookie.setMaxAge(60 * 60 * 24 * 7);
            response.addCookie(refreshCookie);

            // 5) DB 갱신
            user.updateRefreshToken(newRefreshToken);
            userRepository.save(user);

            return ResponseEntity.ok().body("Token reissued successfully");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
    }
}