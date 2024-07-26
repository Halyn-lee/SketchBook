package com.app.sketchbook.user.service;

import com.app.sketchbook.user.DTO.CustomOAuth2User;
import com.app.sketchbook.user.entity.SketchUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ConnectionLogService connectionLogService;
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 접속 로그 기록
        SketchUser user = userService.findUser(oAuth2User.getEmail());
        connectionLogService.insertConnection(request, user);

        // 사용자 정보를 세션에 저장
        request.getSession().setAttribute("username", oAuth2User.getEmail());
        response.sendRedirect("/main");
    }


}
