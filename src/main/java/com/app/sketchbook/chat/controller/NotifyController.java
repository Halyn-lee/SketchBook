package com.app.sketchbook.chat.controller;

import com.app.sketchbook.chat.service.ChatNotifyService;
import com.app.sketchbook.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequiredArgsConstructor
public class NotifyController {

    private final ChatNotifyService chatNotifyService;
    private final UserService userService;

    @GetMapping("/connect-chat-notify")
    public ResponseEntity<SseEmitter> connect(){
        SseEmitter emitter = new SseEmitter();
        var foundUser = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        chatNotifyService.addEmitter(foundUser.getId(), emitter);
        return ResponseEntity.ok(emitter);
    }
}
