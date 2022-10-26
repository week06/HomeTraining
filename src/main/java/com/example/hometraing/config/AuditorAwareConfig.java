package com.example.hometraing.config;

import com.example.hometraing.domain.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;
import java.util.Optional;

@RequiredArgsConstructor
//@EnableJpaAuditing
@Configuration
public class AuditorAwareConfig {

//    @Resource
//    private Member member;
//    private final JPAQueryFactory jpaQueryFactory;
//
//    @Bean
//    public AuditorAware<Long> auditorAware() {
//        return new AuditorAware<>() {
//
//            @Override
//            public Optional<Long> getCurrentAuditor() {
//
//                // 임의로 멤버 1 의 아이디를 가져온다.
//                Member member1 = jpaQueryFactory
//                        .selectFrom(member)
//                        .where(member.id.eq(2L))
//                        .fetchOne();
//
//                Long memberid = member1.getId();
//                return Optional.of(memberid);
//            }
//
//        };
//    }

}

// 로그인 기능 무사히 취합 성공 시 해제
//@Configuration
//@EnableJpaAuditing
//public class SpringSecurityAuditorAware implements AuditorAware<String> {
//
//    @Override
//    public Optional<String> getCurrentAuditor() {
//        /**
//         * SecurityContext 에서 인증정보를 가져와 주입시킨다.
//         * 현재 코드는 현재 Context 유저가 USER 인가 권한이 있으면, 해당 Principal name 을 대입하고, 아니면 Null 을 set 한다.
//         */
//        return Optional.ofNullable(SecurityContextHolder.getContext())
//                .map(SecurityContext::getAuthentication)
//                .map(authentication -> {
//                    Collection<? extends GrantedAuthority> auth = authentication.getAuthorities();
//                    boolean isUser = auth.contains(new SimpleGrantedAuthority("USER"));
//                    if (isUser) return authentication.getName();
//                    return null;
//                });
//    }
//}