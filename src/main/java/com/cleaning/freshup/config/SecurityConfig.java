package com.cleaning.freshup.config;

import com.cleaning.freshup.auth.handler.*;
import com.cleaning.freshup.auth.jwt.*;
import com.cleaning.freshup.auth.oauth.CustomOAuth2UserService;
import com.cleaning.freshup.domain.user.repository.HttpCookieOAuth2AuthorizationRequestRepository;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomOAuth2UserService customOAuth2UserService;
        private final OAuth2SuccessHandler oAuth2SuccessHandler;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // 1. CORS 설정 활성화
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable()) // JWT 환경이므로 보통 disable 합니다.
                                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/login/**", "/oauth2/**", "/reissue").permitAll()
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth -> oauth
                                                .authorizationEndpoint(authorization -> authorization
                                                                .baseUri("/oauth2/authorization")
                                                                // 2. 여기에 쿠키 레포지토리를 설정한다.
                                                                .authorizationRequestRepository(
                                                                                httpCookieOAuth2AuthorizationRequestRepository))
                                                .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                                                .successHandler(oAuth2SuccessHandler))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // 2. CORS 세부 설정 빈 등록
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // 프론트엔드 주소 허용
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
                // 모든 HTTP 메서드 허용
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                // 모든 헤더 허용
                configuration.setAllowedHeaders(Arrays.asList("*"));
                // 자바스크립트에서 쿠키를 포함한 요청을 보낼 수 있도록 허용 (필수)
                configuration.setAllowCredentials(true);
                // 브라우저가 응답의 특정 헤더에 접근할 수 있도록 노출 (필수 아님, 필요시 추가)
                configuration.setExposedHeaders(Arrays.asList("Set-Cookie"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}