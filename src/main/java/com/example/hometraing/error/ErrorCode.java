package com.example.hometraing.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    //400 BAD_REQUEST 잘못된 요청
    INVALID_PARAMETER(400, "파라미터 값을 확인해주세요."),
    PASSWORDS_NOT_MATCHED(400,"비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    NOT_EXIST_TOKEN(400, "인증 정보가 존재하지 않습니다."),
    UNUSEFUL_TOKEN(400, "TOKEN 값이 유효하지 않습니다."),
    NOT_EXIST_BOARD(400, "현재 로그인한 유저가 작성한 게시글이 아닙니다."),

    //409 CONFLICT 중복된 리소스
    ALREADY_SAVED_ID(409, "중복된 아이디입니다."),
    ALERADY_SAVED_NICKNAME(409,"중복된 닉네임입니다."),

    //500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(500, "서버 에러입니다. 고객센터에 문의해주세요"),
    NOT_FOUND_MEMBER(400, "유저 정보가 일치하지 않습니다.");


    private final int status;
    private final String message;
}
