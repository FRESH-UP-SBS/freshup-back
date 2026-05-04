package com.cleaning.freshup.domain.user.dto;

import com.cleaning.freshup.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUserResponseDto {

    private Long id;
    private String name;
    private String role;

    public static CurrentUserResponseDto from(User user) {
        return CurrentUserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}