package com.app.sketchbook.user.service;

import com.app.sketchbook.user.exception.CustomAuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomLoginFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");

        // 계정 비활성화 예외 처리
        if (exception.getMessage().equals("메일 인증이 필요합니다.")) {
            response.sendRedirect("/verify?email=" + email);
        } else {
            response.sendRedirect("/login?error");
        }
    }
}
