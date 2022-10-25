package com.example.hometraing.security;

import com.example.hometraing.domain.Member;
import com.example.hometraing.domain.UserRoleEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Member member;

    @Override
    public String getPassword() {
        System.out.println("유저 디테일 비밀번호 확인 : " + member.getPassword());
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        System.out.println("유저 디테일 이름 확인 : " + member.getNickname());
        return member.getNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum userRole =  UserRoleEnum.USER;
        String authority = userRole.getAuthority();

        SimpleGrantedAuthority simpleAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleAuthority);

        return authorities;
    }
}