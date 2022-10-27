package com.example.hometraing.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberRequestDto {

    // 회원가입 아이디
    @Pattern(regexp ="^[a-zA-Z0-9]+@[a-zA-Z]+.[a-z]+${4,12}$", message = "{memberid.option}" )
    @NotBlank(message = "{memberid.notblank}")
    private String memberid;

    // 회원가입 비밀번호
    @NotBlank(message = "{password.notblank}")
    @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$" , message = "{password.option}" )
    private String password;


    //회원가입 비번확인
    @NotBlank(message = "{password.notblank}")
    @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$" , message = "{password.option}" )
    private String passwordconfirm;

    // 회원가입 닉네임
    @NotBlank(message = "{password.notblank}")
    private String nickname;

}
