package com.example.hometraing.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CommentResponseDto { // 최종적으로 FE로 전달될 json 데이터들
    // 게시글 id
    private Long id;

    // 게시글 작성자 (현재 로그인한 유저 정보)
    private String author;

    // 작성한 게시글 내용
    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
