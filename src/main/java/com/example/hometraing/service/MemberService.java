package com.example.hometraing.service;

import com.example.hometraing.controller.request.MemberRequestDto;
import com.example.hometraing.controller.response.MemberResponseDto;
import com.example.hometraing.controller.response.ResponseDto;
import com.example.hometraing.domain.Member;
import com.example.hometraing.domain.Timestamped;
import com.example.hometraing.repository.MemberRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.hometraing.domain.QMember.member;


@RequiredArgsConstructor
@Service
public class MemberService extends Timestamped {

    private final MemberRepository memberRepository;

    private final JPAQueryFactory jpaQueryFactory;

    // 회원가입
    public ResponseDto<?> memberRegist(MemberRequestDto memberRequestDto) {
        Member member1 = jpaQueryFactory
                .selectFrom(member)
                .where(member.memberid.eq(memberRequestDto.getMemberid()))
                .fetchOne();

        if (member1 != null) {
            return ResponseDto.fail("ALREADY_EXIST_MEMBER", "중복된 계정입니다.");
        }

        if (!memberRequestDto.getPassword().equals(memberRequestDto.getPasswordconfirm())) {
            return ResponseDto.fail("WRONG_MATCH_PASSWORD", "패스워드를 재확인 해주십시오.");
        }

        member1 =
                Member.builder()
                        .memberid(memberRequestDto.getMemberid())
                        .password(memberRequestDto.getPassword())
                        .nickname(memberRequestDto.getNickname())
                        .build();

        memberRepository.save(member1);

        System.out.println("인서트 확인");


        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(member1.getId())
                        .memberid(member1.getMemberid())
                        .password(member1.getPassword())
                        .nickname(member1.getNickname())
                        .build()
        );


//        jpaQueryFactory
//                .insert(member)
//                .columns(member.id, member.memberid, member.password, member.nickname, member.createdAt, member.modifiedAt)
//                .values(member.id, memberRequestDto.getMemberid(), memberRequestDto.getPassword(), memberRequestDto.getNickname(), getCreatedAt(), getModifiedAt())
//                .execute();

    }
}
