package com.example.hometraing.controller;

import com.example.hometraing.controller.request.CommentRequestDto;
import com.example.hometraing.controller.response.ResponseDto;
import com.example.hometraing.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController //프른트랑 잇게 될 경우에Controller로 바꿔야 될 수 있음추후 상황 파악 필요
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    //댓글생성
    @PostMapping(value = "/comment/{board_id}")
    public ResponseDto<?> createComment(@PathVariable Long board_id, @RequestBody CommentRequestDto requestDto,
                                        HttpServletRequest request) {
        return commentService.createComment(board_id, requestDto, request);
    }


    //댓글 조회
//    @GetMapping(value = "/comment/{id}")
//    public ResponseDto<?> getAllComments(@PathVariable Long id) {
//        return commentService.getAllCommentsByBoard(id);
//    }

    //댓글수정
    @PutMapping(value = "/commanet/{id}")
    public ResponseDto<?> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto,
                                        HttpServletRequest request) {
        return commentService.updateComment(id, requestDto, request);
    }

    //댓글삭제
    @DeleteMapping(value = "/comment/{id}")
    public ResponseDto<?> deleteComment(@PathVariable Long id,
                                        HttpServletRequest request) {
        return commentService.deleteComment(id, request);
    }
}


