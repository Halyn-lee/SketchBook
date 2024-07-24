package com.app.sketchbook;

import com.app.sketchbook.user.jwt.CustomSuccessHandler;
import com.app.sketchbook.user.jwt.JWTFilter;
import com.app.sketchbook.user.jwt.JWTUtil;
import com.app.sketchbook.user.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final OAuth2UserService oAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((csrf) -> csrf.disable());

        http
                .formLogin((login) -> login.disable());

        http
                .httpBasic((basic) -> basic.disable());

        //JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(oAuth2UserService))
                        .successHandler(customSuccessHandler)
                );

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated());

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}