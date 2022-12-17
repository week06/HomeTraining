package com.example.hometraing.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BoardRequestDto {

    // 게시글 제목
    private String title;

    // 게시글 내용
    private String content;

    // 게시글 카테고리
    private String category;

}
