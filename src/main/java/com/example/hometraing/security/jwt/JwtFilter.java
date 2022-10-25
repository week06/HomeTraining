package com.example.hometraing.security.jwt;

import com.example.hometraing.controller.response.ResponseDto;
import com.example.hometraing.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static String AUTHORIZATION_HEADER = "Authorization";
    public static String BEARER_PREFIX = "Bearer ";
    public static String AUTHORITIES_KEY = "auth";
    private final String SECRET_KEY;
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;



    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        System.out.println("로그인 시 dofilter 로 오는지 확인");

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        System.out.println("dofiler 속 request : " + request);
        System.out.println("dofiler 속 response : " + response);

        String jwt = resolveToken(request);
        System.out.println("dofilter 진입 후 jwt 생성  : " + jwt);


        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

            Claims claims;

            try {
                claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
                System.out.println("jwt 토큰 해제 : " + claims);
            } catch (ExpiredJwtException e) {
                claims = e.getClaims();
            }

            if (claims.getExpiration().toInstant().toEpochMilli() < Instant.now().toEpochMilli()) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(
                        new ObjectMapper().writeValueAsString(
                                ResponseDto.fail("BAD_REQUEST", "Token이 유효햐지 않습니다.")
                        )
                );
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            String subject = claims.getSubject();

            System.out.println("claims 에서 subjeect 가져오기 : " + subject);

            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            UserDetails principal = userDetailsService.loadUserByUsername(subject);

            System.out.println("userDetailsService 의 아이디 검증 후 UserDetail 에 저장 : " + principal);

            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        System.out.println("resolveToken 속 bearerToken 확인 : " + bearerToken);
        System.out.println("resolveToken 속 request 확인 : " + request);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
