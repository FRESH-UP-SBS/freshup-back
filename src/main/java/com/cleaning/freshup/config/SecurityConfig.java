package com.cleaning.freshup.config;

import com.cleaning.freshup.auth.handler.*;
import com.cleaning.freshup.auth.jwt.*;
import com.cleaning.freshup.auth.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomOAuth2UserService customOAuth2UserService; // OAuth2 사용자 정보를 처리하는 서비스
        private final OAuth2SuccessHandler oAuth2SuccessHandler; // OAuth2 로그인 성공 시 처리하는 핸들러
        private final JwtAuthenticationFilter jwtAuthenticationFilter; // JWT 인증을 처리하는 필터

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf
                                                /**
                                                 * CSRF 보호를 활성화하고, 쿠키 기반 CSRF 토큰 저장소를 사용하도록 설정. HttpOnly 속성은 false로 설정하여
                                                 * 클라이언트 측에서
                                                 */
                                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/login/**", "/oauth2/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth -> oauth
                                                .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                                                .successHandler(oAuth2SuccessHandler))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                /**
                 * JWT 인증 필터를 UsernamePasswordAuthenticationFilter 전에 추가
                 */
                return http.build();
        }
}