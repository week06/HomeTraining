package com.example.hometraing.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;



@AllArgsConstructor
@Getter
@Builder
public class TokenDto {
    private String grantType;
    private String accessToken;
    private Long accessTokenExpiresIn;
    private String refreshToken;
}
