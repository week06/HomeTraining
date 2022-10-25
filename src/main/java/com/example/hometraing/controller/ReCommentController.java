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

    @PostMapping(value = "/recomment")
    public ResponseDto<?> createReComment(@RequestBody ReCommentRequestDto requestDto,
                                        HttpServletRequest request) {
        return reCommentService.createReComment(requestDto, request);
    }

    @PutMapping(value = "/recomment/{id}")
    public ResponseDto<?> updateSubComment(@PathVariable Long id, @RequestBody ReCommentRequestDto requestDto,
                                           HttpServletRequest request) {
        return reCommentService.updateReComment(id, requestDto, request);
    }

    @DeleteMapping(value = "/recomment/{id}")
    public ResponseDto<?> deleteReComment(@PathVariable Long id,
                                        HttpServletRequest request) {
        return reCommentService.deleteReComment(id, request);
    }

//    @GetMapping("/sub-comment")
//    public ResponseDto<?> getAllPostByMember(HttpServletRequest request) {
//        return subCommentService.getAllSubCommentByMember(request);
//    }
}
