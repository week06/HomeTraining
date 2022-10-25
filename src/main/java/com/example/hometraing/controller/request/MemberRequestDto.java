package com.example.hometraing.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@Getter
public class MemberRequestDto {

    @Pattern(regexp ="^[a-zA-Z0-9]+@[a-zA-Z]+.[a-z]+${4,12}$", message = "{memberid.option}" )
    @NotBlank(message = "{memberid.notblank}")
    private String memberid;

    @NotBlank(message = "{password.notblank}")
    @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$" , message = "{password.option}" )
    private String password;

    @NotBlank(message = "{password.notblank}")
    @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$" , message = "{password.option}" )
    private String passwordconfirm;

    @NotBlank(message = "{password.notblank}")
    private String nickname;

}
