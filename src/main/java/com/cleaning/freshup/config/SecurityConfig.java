package com.cleaning.freshup.config;

import com.cleaning.freshup.auth.handler.*;
import com.cleaning.freshup.auth.jwt.*;
import com.cleaning.freshup.auth.oauth.CustomOAuth2UserService;
import com.cleaning.freshup.domain.user.repository.HttpCookieOAuth2AuthorizationRequestRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomOAuth2UserService customOAuth2UserService;
        private final OAuth2SuccessHandler oAuth2SuccessHandler;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        // 1. 위에서 만든 레포지토리를 주입받습니다.
        private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable()) // JWT 환경이므로 보통 disable 합니다.
                                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/login/**", "/oauth2/**").permitAll()
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth -> oauth
                                                .authorizationEndpoint(authorization -> authorization
                                                                .baseUri("/oauth2/authorization")
                                                                // 2. 여기에 쿠키 레포지토리를 설정합니다!
                                                                .authorizationRequestRepository(
                                                                                httpCookieOAuth2AuthorizationRequestRepository))
                                                .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                                                .successHandler(oAuth2SuccessHandler))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}

// @Bean
// public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
// http
// // 1. CSRF는 필요에 따라 disable하거나 설정을 유지하되,
// // OAuth2 콜백 경로는 확실히 permitAll 되어야 함
// .csrf(csrf -> csrf.disable())

// // 2. 세션은 STATELESS로 유지 (JWT 서버니까)
// .sessionManagement(s ->
// s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

// .authorizeHttpRequests(auth -> auth
// .requestMatchers("/", "/login/**", "/oauth2/**", "/favicon.ico")
// .permitAll()
// .anyRequest().authenticated())

// // 3. OAuth2 설정 보완
// .oauth2Login(oauth -> oauth
// .authorizationEndpoint(a -> a
// // 세션 대신 쿠키에 인증 요청 정보를 저장하도록 설정 (KOE237 해결책)
// .authorizationRequestRepository(
// cookieAuthorizationRequestRepository()))
// .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
// .successHandler(oAuth2SuccessHandler))

// .addFilterBefore(jwtAuthenticationFilter,
// UsernamePasswordAuthenticationFilter.class);

// return http.build();
// }

// // 쿠키 저장소 빈 등록
// @Bean
// public HttpCookieOAuth2AuthorizationRequestRepository
// cookieAuthorizationRequestRepository() {
// return new HttpCookieOAuth2AuthorizationRequestRepository();
// }
// }