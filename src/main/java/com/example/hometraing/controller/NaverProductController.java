package com.example.hometraing.controller;

import com.example.hometraing.controller.request.NaverRequestVariableDto;
import com.example.hometraing.service.NaverProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@RequestMapping("/api")
@Controller
public class NaverProductController {

    // 네이버 서비스 호출
    private final NaverProductService naverProductService;

    @ResponseBody
    @GetMapping("/product")
    public ResponseEntity<?> naverProduct(@RequestParam("product") String product){ // 네이버 쇼핑 검색 상품 키워드

        // 상품 키워드, 보여지게될 상품 개수 등을 설정
        NaverRequestVariableDto naverRequestVariableDto = NaverRequestVariableDto.builder()
                .query(product)
                .display(9)
                .start(1)
                .sort("sim")
                .build();

        return naverProductService.naverShopSearchAPI(naverRequestVariableDto);
    }
}
