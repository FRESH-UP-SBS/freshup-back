package com.cleaning.freshup.domain.userstats.controller;

import com.cleaning.freshup.domain.userstats.dto.MyPageStatsResponseDto;
import com.cleaning.freshup.domain.userstats.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-stats")
@RequiredArgsConstructor
public class UserStatsController {

    private final UserStatsService userStatsService;

    @GetMapping("/{userId}")
    public MyPageStatsResponseDto getUserStats(@PathVariable Long userId) {
        return userStatsService.getMyPageStats(userId);
    }
}