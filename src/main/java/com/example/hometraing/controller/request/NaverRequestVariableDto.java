package com.example.hometraing.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class NaverRequestVariableDto {

    private String query;
    private Integer display;
    private Integer start;
    private String sort;

}