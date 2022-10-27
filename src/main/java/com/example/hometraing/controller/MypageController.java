package com.example.hometraing.controller;

import com.example.hometraing.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RequestMapping("/api/my")
@Controller
public class MypageController {

    private final MyPageService mypageService;

    // 작성한 게시글 목록 조회
    @ResponseBody
    @GetMapping("/boards")
    public ResponseEntity<?> getAllMyBoards(HttpServletRequest request) { // 요청 인증 정보를 위한 HttpServletRequest
        return mypageService.getAllMyBoards(request);
    }

    // 작성한 게시글 상세조회
    @ResponseBody
    @PostMapping("/board/{boardid}")
    public ResponseEntity<?> getMyBoard(@PathVariable Long boardid, // 상세 조회하고자 하는 게시글 id
                                        HttpServletRequest request) { // 요청 인증 정보를 위한 HttpServletRequest
        return mypageService.getMyBoard(boardid, request);
    }

    // 작성한 댓글들 목록 조회
    @ResponseBody
    @GetMapping(value = "/comments")
    public ResponseEntity<?> getAllMyComments(HttpServletRequest request) { // 요청 인증 정보를 위한 HttpServletRequest
        return mypageService.getAllMyComments(request);
    }

    // 작성한 댓글 상세 조회
    @ResponseBody
    @PostMapping(value = "/comment/{commentid}")
    public ResponseEntity<?> getMyComment(@PathVariable Long commentid, // 상세 조회하고자 하는 댓글 id
                                          HttpServletRequest request) { // 요청 인증 정보를 위한 HttpServletRequest
        return mypageService.getMyComment(commentid, request);
    }


}
