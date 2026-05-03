package com.cleaning.freshup.auth.oauth;

import java.util.Collections;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);
        KakaoOAuth2UserInfo info = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());

        User user = userRepository.findByProviderAndProviderId("kakao", info.getId())
                .map(u -> u.updateNickname(info.getNickname())) // 기존 사용자가 로그인할 때마다 닉네임 업데이트
                .orElseGet(() -> userRepository.save(User.builder() // 새로운 사용자는 받은 정보 저장(save는 JpaRepository에서 상속 받은 함수)
                        .email(info.getEmail() != null ? info.getEmail() : "")
                        .nickname(info.getNickname())
                        .provider("kakao")
                        .providerId(info.getId())
                        .role(User.Role.USER)
                        .build()));

        return new DefaultOAuth2User(
                Collections.singleton(() -> user.getRole().name()),
                oAuth2User.getAttributes(),
                "id");
    }
}
