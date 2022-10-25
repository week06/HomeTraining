package com.example.hometraing.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class MemberResponseDto {
    private Long id;
    private String memberid;
    private String password;
    private String nickname;
}
