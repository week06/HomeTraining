package com.example.hometraing.security.jwt;

import com.example.hometraing.controller.response.ResponseDto;
import com.example.hometraing.controller.response.TokenDto;
import com.example.hometraing.domain.Member;
import com.example.hometraing.domain.RefreshToken;
import com.example.hometraing.domain.UserRoleEnum;
import com.example.hometraing.repository.RefreshTokenRepository;
import com.example.hometraing.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 10000 * 60 * 30;            //30분
    private static final long REFRESH_TOKEN_EXPRIRE_TIME = 10000 * 60 * 60 * 24 * 7;     //7일

    private final Key key;

    private final RefreshTokenRepository refreshTokenRepository;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDto(Member member) {
        long now = (new Date().getTime());

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
                .setSubject(member.getMemberid())
                .claim(AUTHORITIES_KEY, UserRoleEnum.USER) // 관리자 / 일반 유저 구분 설정은 따로 하지 않음. 추후, 권한 설정을 설계한다 하면 enum으로 설정 추가 필요
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPRIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        System.out.println();
        System.out.println("AccessToken 생성 확인 : " + accessToken);
        System.out.println("refreshToken 생성 확인 : " + refreshToken);
        System.out.println();

        RefreshToken refreshTokenObject = RefreshToken.builder()
                .id(member.getId())
                .member(member)
                .value(refreshToken)
                .build();

        refreshTokenRepository.save(refreshTokenObject);

        return TokenDto.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();

    }

//  public Authentication getAuthentication(String accessToken) {
//    Claims claims = parseClaims(accessToken);
//
//    if (claims.get(AUTHORITIES_KEY) == null) {
//      throw new RuntimeException("권한 정보가 없는 토큰 입니다.");
//    }
//
//    Collection<? extends GrantedAuthority> authorities =
//        Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
//            .map(SimpleGrantedAuthority::new)
//            .collect(Collectors.toList());
//
//    UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());
//
//    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
//  }

    public Member getUserFromAuthentication() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return null;
        }

        return ((UserDetailsImpl) authentication.getPrincipal()).getMember();
    }

    public boolean validateToken(String token) {
        try {
            System.out.println("validate 속 token : " + token);
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

//  private Claims parseClaims(String accessToken) {
//    try {
//      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
//    } catch (ExpiredJwtException e) {
//      return e.getClaims();
//    }
//  }

    @Transactional(readOnly = true)
    public RefreshToken isPresentRefreshToken(Member member) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMember(member);
        return optionalRefreshToken.orElse(null);
    }


    // 로그아웃 시 refreshToken 을 삭제하는 메소드, responseDto 형식을 지양해야 할 수도 있기 때문에 일단 주석처리
    @Transactional
    public ResponseDto<?> deleteRefreshToken(Member member) {
        RefreshToken refreshToken = isPresentRefreshToken(member);
        if (null == refreshToken) {
            return ResponseDto.fail("TOKEN_NOT_FOUND", "존재하지 않는 Token 입니다.");
        }

        refreshTokenRepository.delete(refreshToken);
        return ResponseDto.success("success");
    }
}
