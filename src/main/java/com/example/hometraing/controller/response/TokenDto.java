package com.example.hometraing.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;



@AllArgsConstructor
@Getter
@Builder
public class TokenDto {
    // 권한 타입
    private String grantType;

    // 액세스 토큰
    private String accessToken;

    // 액세스 토큰 만료 기간
    private Long accessTokenExpiresIn;

    // 리프레시 토큰
    private String refreshToken;
}
