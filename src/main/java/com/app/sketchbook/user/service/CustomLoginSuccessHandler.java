package com.app.sketchbook.user.service;

import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService;
    private final ConnectionLogService connectionLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String isFindUser = authentication.getName();
        SketchUser user = this.userService.findUser(isFindUser);

        // 접속 로그 기록
        connectionLogService.insertConnection(request, user);

        if(user.getUpdate_pw()) {
            request.getSession().setAttribute("username", authentication.getName());
            response.sendRedirect("/updatepassword");
        }else {
            request.getSession().setAttribute("username", authentication.getName());
            //request.getSession().setAttribute("authorities", authentication.getAuthorities());
            response.sendRedirect("/main");
        }
    }
}
