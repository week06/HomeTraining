package com.example.hometraing.controller;

import com.example.hometraing.controller.request.LoginRequestDto;
import com.example.hometraing.controller.request.MemberRequestDto;
import com.example.hometraing.controller.response.ResponseDto;
import com.example.hometraing.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController //프른트랑 잇게 될 경우에Controller로 바꿔야 될 수 있음추후 상황 파악 필요
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseDto<?> memberRegist(@RequestBody MemberRequestDto memberRequestDto){
        System.out.println("아이디 : " + memberRequestDto.getMemberid());
        System.out.println("비밀번호 : " + memberRequestDto.getPassword());
        System.out.println("닉네임 : " + memberRequestDto.getNickname());

        return memberService.memberRegist(memberRequestDto);
    }

    // 로그인
//    @PostMapping(value = "/login")
//    public  ResponseDto<?> login (@RequestBody @Valid LoginRequestDto requestDto, HttpServletResponse response ) {
//        return memberService.login(requestDto , response);
//    }

    @PostMapping(value = "/login")
    public  ResponseDto<?> login (@RequestBody @Valid LoginRequestDto requestDto, HttpServletResponse response ) {
        return memberService.login(requestDto , response);
    }

    //로그아웃
    @PostMapping(value="/logout")
    public ResponseDto<?> logout(HttpServletRequest request) {
        return memberService.logout(request);
    }

}
