package com.cleaning.freshup.domain.user.controller;

import com.cleaning.freshup.domain.user.dto.CurrentUserResponseDto;
import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/me")
    public CurrentUserResponseDto getCurrentUser(@AuthenticationPrincipal String email) {
        // @AuthenticationPrincipal로 이메일을 바로 받아오면 코드가 더 깔끔해집니다.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음"));

        return CurrentUserResponseDto.from(user);
    }
}