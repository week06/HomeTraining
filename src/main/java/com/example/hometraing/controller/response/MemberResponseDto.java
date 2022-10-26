package com.example.hometraing.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class MemberResponseDto {
    // 멤버 고유 id
    private Long id;

    // 멤버 아이디
    private String memberid;

    // 멤버 패스워드
    private String password;

    // 멤버 닉네임
    private String nickname;
}
