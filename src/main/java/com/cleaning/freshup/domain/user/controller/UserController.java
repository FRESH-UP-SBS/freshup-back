package com.cleaning.freshup.domain.user.controller;

import com.cleaning.freshup.domain.user.dto.CurrentUserResponseDto;
import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public CurrentUserResponseDto getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        String email = authentication.getName();

        if ("anonymousUser".equals(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "사용자 정보를 찾을 수 없습니다."
                ));

        return CurrentUserResponseDto.from(user);
    }
}