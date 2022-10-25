package com.example.hometraing.security;

import com.example.hometraing.domain.Member;
import com.example.hometraing.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    public UserDetails loadUserByUsername(String memberid) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberid(memberid);

        return new UserDetailsImpl(member);
    }
}