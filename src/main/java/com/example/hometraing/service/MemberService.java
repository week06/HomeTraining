package com.example.hometraing.service;

import com.example.hometraing.controller.request.LoginRequestDto;
import com.example.hometraing.controller.request.MemberRequestDto;
import com.example.hometraing.controller.response.MemberResponseDto;
import com.example.hometraing.controller.response.ResponseDto;
import com.example.hometraing.controller.response.TokenDto;
import com.example.hometraing.domain.Member;
import com.example.hometraing.domain.Timestamped;
import com.example.hometraing.repository.MemberRepository;
import com.example.hometraing.security.jwt.TokenProvider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.example.hometraing.domain.QMember.member;


@RequiredArgsConstructor
@Service
public class MemberService extends Timestamped {

    private final MemberRepository memberRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

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
                        .password(passwordEncoder.encode(memberRequestDto.getPassword()))
                        .nickname(memberRequestDto.getNickname())
                        .build();

        memberRepository.save(member1);

        System.out.println("인서트 확인");


        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(member1.getId())
                        .memberid(member1.getMemberid())
                        .nickname(member1.getNickname())
                        .build()
        );

    }


    @Transactional /* 로그인 - 유저 아이디 유효성 체크*/
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        Member member = isPresentMember(requestDto.getMemberid());
        if (null == member) {  // member가 null이면 "MEMBER_NOT_FOUND" , "사용자를 찾을 수 없습니다"  예외처리
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "사용자를 찾을 수 없어여.");
        }
        
        /*  비밀번호 유효성 검증  - Jwt token 을 encoding한 내용에 들어있는 비밀번호와  Dto에서 가저온 비밀번호가 같지 않으면 "INVALIED_MEMEBER" , "사용자를 찾을 수 없습니다." 예외처리 */
        if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
            return ResponseDto.fail("INVALID_MEMBER", "사용자를 찾을 수 없습니다.");
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenToHeaders(tokenDto, response);

        return ResponseDto.success( //ResponseDto 로 success 리턴 할때, MemberResponseDto.builder() 에 id, nickname, createdAt, modifiedAt, email을 담아서 리턴
                MemberResponseDto.builder()
                        .id(member.getId()) // member 에서 id를 가저오고
                        .memberid(member.getMemberid()) // member에서 멤버id 가져오고
                        .nickname(member.getNickname())
                        .build()
        );
    }


    public ResponseDto<?> logout (HttpServletRequest request){
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {  //tokenProvider에서 가저온 토큰값이 heaader에서 가저온 Refresh-Token이 아니라면,
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다."); //"INVALID_TOKEN", "Token이 유효하지 않습니다" ResponseDto로 리턴
        }
        Member member = (Member) tokenProvider.getMemberFromAuthentication();
        if (null == member) { //member가 null이라면, "MEMBER_NOT_FOUND", "사용자를 찾을 수 없습니다" ResponseDto로 리턴
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "사용자를 찾을 수 없습니다.");
        }
        return tokenProvider.deleteRefreshToken(member); // deleteRefreshToken
    }

    /* readOnly = true: 데이터의 변경이 없는 읽기 전용 메서드에 사용, 영속성 컨텍스트를 플러시 하지 않으므로 약간의 성능 향상*/
    @Transactional(readOnly = true)
    public Member isPresentMember(String memberid) {
        Optional<Member> optionalMember = Optional.ofNullable(memberRepository.findByMemberid(memberid));
        return optionalMember.orElse(null);
    }

    public void tokenToHeaders (TokenDto tokenDto, HttpServletResponse response) {  // jwt Token header에
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken()); // "Authorization" , "Bearer + 토큰Dto에서가저온 AccessToken" 을 header에 add
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken()); // "Refresh-Token", "tokenDto에서 가저온 RefreshToken을 hearder에 add
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString()); // "Access-Token-Expire-Time" , "tokenDto에서 가저온 토큰만료시간을 string타입으로 add.
    }

  }



