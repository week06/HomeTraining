package com.example.hometraing.security;

import com.example.hometraing.config.CorsConfig;
import com.example.hometraing.security.jwt.AccessDeniedHandlerException;
import com.example.hometraing.security.jwt.AuthenticationEntryPointException;
import com.example.hometraing.security.jwt.TokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebSecurityConfig {

    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationEntryPointException authenticationEntryPointException;
    private final AccessDeniedHandlerException accessDeniedHandlerException;
    private final CorsConfig corsConfig;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String SECRET_KEY = "c3ByaW5nLWJvb3Qtc2VjdXJpdHktand0LWhhbmdoYWUtYXNzaWdubWVudC1zcHJpbmctYm9vdC1zZWN1cml0eS1qd3Qtc2VjcmV0LWtleQo=";
        http.cors();

        http.csrf().disable()

                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointException)
                .accessDeniedHandler(accessDeniedHandlerException)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                // 인증 정보를 통해야지만 동작할 수 있는 url은 public 대신 auth 로 만든다.
                .antMatchers("/api/member/signup").permitAll()
                .antMatchers("/api/member/login").permitAll()
                .antMatchers("/api/board/**").permitAll()
                .antMatchers("/api/boards/**").permitAll()
                .antMatchers("/api/**").permitAll()

//                .antMatchers("/api/boards/**").permitAll()
//                .antMatchers("/board/public/**").permitAll()
//                .antMatchers("/comment/public/**").permitAll()
                .antMatchers("/v2/api-docs",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/assets/**",
                        "/js/**",
                        "/css/**"
                ).permitAll()
                .anyRequest().authenticated()

                .and()
                .addFilter(corsConfig.corsFilter())
                .apply(new JwtSecurityConfiguration(SECRET_KEY, tokenProvider, userDetailsService));

        return http.build();
    }
}
