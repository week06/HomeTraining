package com.example.hometraing.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReCommentRequestDto {
    private Long commentId;
    private String content;
}

