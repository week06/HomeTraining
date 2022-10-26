package com.example.hometraing.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    // 멤버 아이디
    @NotBlank
    private String memberid;

    // 멤버 비밀번호
    @NotBlank
    private String password; // password


}