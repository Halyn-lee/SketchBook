package com.app.sketchbook;


import com.app.sketchbook.user.service.CustomLoginFailHandler;
import com.app.sketchbook.user.service.CustomLoginSuccessHandler;
import com.app.sketchbook.user.service.CustomOAuth2LoginSuccessHandler;
import com.app.sketchbook.user.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final OAuth2UserService oAuth2UserService;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((csrf) -> csrf.disable());

        http
                .formLogin((login) -> login.disable());

        http
                .httpBasic((basic) -> basic.disable());

        http
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login")
                        .successHandler(new CustomOAuth2LoginSuccessHandler())
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(oAuth2UserService)));
        http
                .formLogin((formLogin) -> formLogin
                        .loginPage("/login")
                        .successHandler(new CustomLoginSuccessHandler())
                        .failureHandler(new CustomLoginFailHandler())) // 로그인 실패 시 CustomAuthenticationFailureHandler 사용

                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true))
        ;

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login","/join","/join_success","/verify","/verify-success","/verify-failure","/check-email","/resend-activation-email").permitAll()
                        //.requestMatchers("/**").permitAll()
                        .anyRequest().authenticated());


        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}