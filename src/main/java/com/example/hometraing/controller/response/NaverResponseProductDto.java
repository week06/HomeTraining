package com.example.hometraing.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class NaverResponseProductDto {

    // 네이버 제품 검색 시 나오는 상품 제목
    private String title;

    // 상품 링크
    private String link;

    // 상품 이미지
    private String image;

    // 상품 최저가
    private Integer lprice;

}
