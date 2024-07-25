package com.app.sketchbook.chat.service;

import com.app.sketchbook.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Log
@Service
@RequiredArgsConstructor
public class ChatNotifyServiceImpl implements ChatNotifyService {
    Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private final ChatRoomRepository chatRoomRepository;

    public void addEmitter(String user, SseEmitter emitter){

        emitters.put(user, emitter);

        emitter.onCompletion(() -> {
            emitters.remove(user);
        });

        emitter.onTimeout(emitter::complete);
    }

    @Override
    public void notifyChat(String room, String sender) {

        var users = chatRoomRepository.findById(Long.parseLong(room));

        if(users.isEmpty()){
            return;
        }

        try{
            if(users.get().getRequester().getId() == Integer.parseInt(sender)){
                emitters.get(users.get().getReceiver().getId().toString()).send(SseEmitter.event().name("chat").data(room));
            } else {
                emitters.get(users.get().getReceiver().getId().toString()).send(SseEmitter.event().name("chat").data(room));
            }
        } catch (IOException e){
            log.info(e.getMessage());
        }
    }
}
