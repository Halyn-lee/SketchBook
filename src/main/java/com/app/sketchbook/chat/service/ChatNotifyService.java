package com.app.sketchbook.chat.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ChatNotifyService{
    void notifyChat(String room, String sender);
    void addEmitter(String user, SseEmitter emitter);
}
