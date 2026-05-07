package com.cleaning.freshup.domain.userstats.controller;

import com.cleaning.freshup.domain.user.entity.User;
import com.cleaning.freshup.domain.user.repository.UserRepository;
import com.cleaning.freshup.domain.userstats.dto.MyPageStatsResponseDto;
import com.cleaning.freshup.domain.userstats.service.UserStatsService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-stats")
public class UserStatsController {

    private final UserStatsService userStatsService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public MyPageStatsResponseDto getMyStats(@AuthenticationPrincipal String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음"));

        return userStatsService.getMyPageStats(user.getId());
    }

    @GetMapping("/{userId}")
    public MyPageStatsResponseDto getUserStats(@PathVariable Long userId) {
        return userStatsService.getMyPageStats(userId);
    }
}