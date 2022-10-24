package com.example.hometraing.controller;

import com.example.hometraing.controller.request.SubCommentRequestDto;
import com.example.hometraing.controller.response.ResponseDto;
import com.example.hometraing.service.SubCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

//@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SubCommentController {

    private final SubCommentService subCommentService;

    //대댓글 작성
    @PostMapping(value = "/sub-comment")
    public ResponseDto<?> createComment(@RequestBody SubCommentRequestDto requestDto,
                                        HttpServletRequest request) {
        return subCommentService.createSubComment(requestDto, request);
    }

    //대댓글 수정
    @PutMapping(value = "/sub-comment/{id}")
    public ResponseDto<?> updateSubComment(@PathVariable Long id, @RequestBody SubCommentRequestDto requestDto,
                                           HttpServletRequest request) {
        return subCommentService.updateSubComment(id, requestDto, request);
    }

    //대댓글 삭제
    @DeleteMapping(value = "/sub-comment/{id}")
    public ResponseDto<?> createComment(@PathVariable Long id,
                                        HttpServletRequest request) {
        return subCommentService.deleteSubComment(id, request);
    }

//    @GetMapping("/sub-comment")
//    public ResponseDto<?> getAllPostByMember(HttpServletRequest request) {
//        return subCommentService.getAllSubCommentByMember(request);
//    }
}
