package com.example.hometraing.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRoleEnum {

    USER("ROLE_USER"),  // 사용자 권한
    ADMIN("ROLE_ADMIN");  // 관리자 권한

    private final String authority;

    public String getAuthority() {
        return this.authority;
    }

}