package com.app.sketchbook.chat.controller;

import com.app.sketchbook.chat.dto.Chat;
import com.app.sketchbook.chat.entity.ChatLog;
import com.app.sketchbook.chat.service.ChatLogService;
import com.app.sketchbook.chat.service.ChatRoomService;
import com.app.sketchbook.chat.service.KafkaChatProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Log
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final KafkaChatProducer producer;
    private final ChatLogService chatLogService;
    private final ChatRoomService chatRoomService;

    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }

    @MessageMapping("/send")
    public void sendMessage(Chat chat) {
        producer.sendMessage(chat); // Kafka에 메시지 전송
    }

    @MessageMapping("/history/{room}")
    @SendTo("/topic/history/{room}")
    public List<ChatLog> fetchChatLog(@DestinationVariable String room){
        // MongoDB에서 기록 얻어오기
        return chatLogService.getRecentLogs(room);
    }

    @MessageMapping("/disconnect/{room}")
    public void updateDisconnectTime(@DestinationVariable String room){
        chatRoomService.updateDisconnectTime(Long.parseLong(room));
    }

}