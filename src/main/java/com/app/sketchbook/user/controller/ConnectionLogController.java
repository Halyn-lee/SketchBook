package com.app.sketchbook.user.controller;

import com.app.sketchbook.user.service.ConnectionLogService;
import com.app.sketchbook.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Log
@Controller
@RequiredArgsConstructor
public class ConnectionLogController {
    private final UserService userService;
    private final ConnectionLogService connectionLogService;

    @GetMapping("/connection-log")
    public String connectionLogDefault(Model model) {
        return connectionLog(0, model);
    }

    @GetMapping("/connection-log/{page}")
    public String connectionLog(@PathVariable(value = "page") int page, Model model) {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null){
            return "redirect:/login";
        }

        var user = userService.principalUser(auth);

        if(user == null) {
            return "redirect:/login";
        }

        var logs = connectionLogService.findAllLogsByUser(page, user);

        int start = Math.max(page-2, 0);

        model.addAttribute("start_page", start);
        model.addAttribute("end_page", Math.min(start+4, logs.getTotalPages()-1));

        model.addAttribute("logs",logs.get().toList());
        model.addAttribute("current_page",page);
        model.addAttribute("total_page", logs.getTotalPages()-1);

        return "connection-log";
    }
}
