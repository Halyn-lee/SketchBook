package com.app.sketchbook.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String UserLogin(){
        return "login";
    }

    @GetMapping("/my")
    public String myPage() {

        return "my";
    }
}
