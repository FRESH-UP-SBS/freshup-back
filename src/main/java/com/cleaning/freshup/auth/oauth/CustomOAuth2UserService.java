package com.cleaning.freshup.auth.oauth;

import java.util.Collections;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleaning.freshup.domain.user.entity.SocialAccount;
import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.SocialAccountRepository;
import com.cleaning.freshup.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
        private final UserRepository userRepository;
        private final SocialAccountRepository socialAccountRepository;

        @Override
        @Transactional
        public OAuth2User loadUser(OAuth2UserRequest request) {
                OAuth2User oAuth2User = super.loadUser(request);

                KakaoOAuth2UserInfo info = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());

                String provider = "kakao";
                Long id = (Long) oAuth2User.getAttribute("id");
                String providerUserId = id.toString();

                // 1. 소셜 계정 조회
                SocialAccount socialAccount = socialAccountRepository
                                .findByProviderAndProviderUserId(provider, providerUserId)
                                .orElseGet(() -> {
                                        // 2. 없으면 User 생성
                                        User newUser = userRepository.save(User.builder()
                                                        .email(info.getEmail() != null ? info.getEmail() : "")
                                                        .name(info.getNickname())
                                                        .role(User.Role.USER)
                                                        .build());

                                        // 3. SocialAccount 생성
                                        return socialAccountRepository.save(
                                                        SocialAccount.builder()
                                                                        .provider(provider)
                                                                        .providerUserId(providerUserId)
                                                                        .user(newUser)
                                                                        .build());
                                });

                User user = socialAccount.getUser();

                // 닉네임 업데이트 (선택)
                user.updateName(info.getNickname());

                return new DefaultOAuth2User(
                                Collections.singleton(() -> user.getRole().name()),
                                oAuth2User.getAttributes(),
                                "id");
        }
}
