package com.example.hometraing.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;

    // 게시글 작성자 (현재 로그인한 유저 정보)
    private String author;

    // 작성한 게시글 내용
    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
