package com.example.hometraing.domain;


public enum Category {

    ARM(0, "팔"),
    SHOULDER(1, "어깨"),
    BACK(2, "등"),
    ABS(3, "복근"),
    CHEST(4, "가슴"),
    LEG(5, "다리"),
    ALLBODY(6, "전신");

    private int num;
    private String parts;

    Category(int num, String parts){
        this.num = num;
        this.parts = parts;
    }

}
