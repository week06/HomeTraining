//package com.example.hometraing.controller;
//
//import com.example.hometraing.controller.response.ResponseDto;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.servlet.http.HttpServletRequest;
//@RequestMapping("/api/my")
//public class MypageController {
//
//    private final MypageService mypageService;
//
//
//    @PostMapping(value = "/boards")
//    public ResponseDto<?> seepost(@RequestBody BoardRequestDto requestDto,
//                                     HttpServletRequest request) {
//        return mypageService.createPost(requestDto, request);
//    }
//    @PostMapping(value = "/board")
//    public ResponseDto<?> viewpost(@RequestBody BoardRequestDto requestDto,
//                                     HttpServletRequest request) {
//        return mypageServoce.createPost(requestDto, request);
//    }
//    @PostMapping(value = "/comments")
//    public ResponseDto<?> seecomment(@RequestBody CommentRequestDto requestDto,
//                                     HttpServletRequest request) {
//        return mypageService.createPost(requestDto, request);
//    }
//    @PostMapping(value = "/comment")
//    public ResponseDto<?> mycomment(@RequestBody CommentRequestDto requestDto,
//                                     HttpServletRequest request) {
//        return mypageService.createPost(requestDto, request);
//    }
//
//
//}
