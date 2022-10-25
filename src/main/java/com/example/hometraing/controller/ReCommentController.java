package com.example.hometraing.controller;

import com.example.hometraing.controller.request.ReCommentRequestDto;
import com.example.hometraing.controller.response.ResponseDto;
import com.example.hometraing.service.ReCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

//@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ReCommentController {

    private final ReCommentService reCommentService;

    //대댓글 작성
    @PostMapping(value = "/recomment")
    public ResponseDto<?> createComment(@RequestBody ReCommentRequestDto requestDto,
                                        HttpServletRequest request) {
        return reCommentService.createReComment(requestDto, request);
    }

    //대댓글 수정
    @PutMapping(value = "/recomment/{id}")
    public ResponseDto<?> updateSubComment(@PathVariable Long id, @RequestBody ReCommentRequestDto requestDto,
                                           HttpServletRequest request) {
        return reCommentService.updateReComment(id, requestDto, request);
    }

    //대댓글 삭제
    @DeleteMapping(value = "/recomment/{id}")
    public ResponseDto<?> createComment(@PathVariable Long id,
                                        HttpServletRequest request) {
        return reCommentService.deleteReComment(id, request);
    }

//    대댓글 조회
//    @GetMapping("/recomment")
//    public ResponseDto<?> getAllPostByMember(HttpServletRequest request) {
//        return reCommentService.getAllReCommentByMember(request);
//    }
}
