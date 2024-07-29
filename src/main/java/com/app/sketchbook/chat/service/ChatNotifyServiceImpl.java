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

        var foundRoom = chatRoomRepository.findById(Long.parseLong(room));

        if(foundRoom.isEmpty()){
            return;
        }

        try{
            var friend = foundRoom.get().getFriend();

            if(friend.getFrom().getId() == Integer.parseInt(sender)){
                emitters.get(friend.getTo().getId().toString()).send(SseEmitter.event().name("chat").data(room));
            } else {
                emitters.get(friend.getFrom().getId().toString()).send(SseEmitter.event().name("chat").data(room));
            }
        } catch (IOException e){
            log.info(e.getMessage());
        }
    }
}
