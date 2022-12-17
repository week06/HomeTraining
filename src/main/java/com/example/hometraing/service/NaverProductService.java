package com.example.hometraing.service;

import com.example.hometraing.controller.request.NaverRequestVariableDto;
import com.example.hometraing.controller.response.NaverResponseProductDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NaverProductService {

    public ResponseEntity<?> naverShopSearchAPI(NaverRequestVariableDto naverVariable) {

        String url = "https://openapi.naver.com/";

        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .path("v1/search/shop.json")
                .queryParam("query", naverVariable.getQuery())
                .queryParam("display", naverVariable.getDisplay())
                .queryParam("start", naverVariable.getStart())
                .queryParam("sort", naverVariable.getSort())
                .encode()
                .build()
                .toUri();

        log.info("uri : {}", uri);

        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", "c3RO7dzpIG9LJLQ97Rul")
                .header("X-Naver-Client-Secret", "z5KelmNE5N")
                .build();

        ResponseEntity<String> result = restTemplate.exchange(req, String.class);
        List<NaverResponseProductDto> naverProductDto = fromJSONtoNaverProduct(result.getBody());

        log.info("result ={}", naverProductDto);

        return ResponseEntity.ok(naverProductDto);

    }
    private List<NaverResponseProductDto> fromJSONtoNaverProduct(String result) {
        // 문자열 정보를 JSONObject로 바꾸기
        JSONObject rjson = new JSONObject(result);
        // JSONObject에서 items 배열 꺼내기
        // JSON 배열이기 때문에 보통 배열이랑 다르게 활용해야한다.
        JSONArray naverProducts = rjson.getJSONArray("items");
        List<NaverResponseProductDto> naverProductDtoList = new ArrayList<>();
        for (int i = 0; i < naverProducts.length(); i++) {

            JSONObject naverProductsJson = (JSONObject) naverProducts.get(i);

            NaverResponseProductDto itemDto = NaverResponseProductDto.builder()
                    .title(naverProductsJson.getString("title"))
                    .link(naverProductsJson.getString("link"))
                    .image(naverProductsJson.getString("image"))
                    .lprice(naverProductsJson.getInt("lprice"))
                    .build();

            naverProductDtoList.add(itemDto);
        }
        return naverProductDtoList;
    }
}
