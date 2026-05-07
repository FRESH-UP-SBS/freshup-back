package com.cleaning.freshup.domain.user.dto;

import com.cleaning.freshup.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserOptionResponseDto {

    private Long id;
    private String name;

    public static UserOptionResponseDto from(User user) {
        return UserOptionResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}