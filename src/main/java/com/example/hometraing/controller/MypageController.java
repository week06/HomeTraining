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
    public ResponseEntity<?> getAllMyBoards(HttpServletRequest request) {
        return mypageService.getAllMyBoards(request);
    }

    // 작성한 게시글 상세조회
    @ResponseBody
    @PostMapping("/board/{boardid}")
    public ResponseEntity<?> getMyBoard(@PathVariable Long boardid, HttpServletRequest request) {
        return mypageService.getMyBoard(boardid, request);
    }

    // 작성한 댓글들 목록 조회
    @ResponseBody
    @GetMapping(value = "/comments")
    public ResponseEntity<?> getAllMyComments(HttpServletRequest request) {
        return mypageService.getAllMyComments(request);
    }

    // 작성한 댓글 상세 조회
    @ResponseBody
    @PostMapping(value = "/comment/{commentid}")
    public ResponseEntity<?> getMyComment(@PathVariable Long commentid, HttpServletRequest request) {
        return mypageService.getMyComment(commentid, request);
    }


}
