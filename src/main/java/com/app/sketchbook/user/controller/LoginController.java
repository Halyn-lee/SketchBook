package com.app.sketchbook.user.controller;

import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String UserLogin(){
        return "login";
    }

    @PostMapping("/resend-activation-email")
    public String resendActivationEmail(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        SketchUser user = userService.findUser(email);
        try {
            userService.sendVerificationEmail(user);
            redirectAttributes.addFlashAttribute("message", "인증 코드 이메일이 재발송되었습니다. 이메일을 확인해 주세요.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "이메일 전송에 실패했습니다. 다시 시도해 주세요.");
        }
        return "redirect:/verify";
    }
//    @PostMapping("/login")
//    public String loadLogin()
}
