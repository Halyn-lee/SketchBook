package com.app.sketchbook.chat.controller;

import com.app.sketchbook.chat.service.ChatNotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequiredArgsConstructor
public class NotifyController {

    private final ChatNotifyService chatNotifyService;

    @GetMapping("/connect-chat-notify/{user}")
    public ResponseEntity<SseEmitter> connect(@DestinationVariable String user){
        SseEmitter emitter = new SseEmitter();
        chatNotifyService.addEmitter(user, emitter);
        return ResponseEntity.ok(emitter);
    }
}
