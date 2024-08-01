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
    Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    private final ChatRoomRepository chatRoomRepository;

    public void addEmitter(Long user, SseEmitter emitter){

        if(emitters.containsKey(user)){
            return;
        }

        emitters.put(user, emitter);

        emitter.onCompletion(() -> {
            emitters.remove(user);
        });

        emitter.onTimeout(emitter::complete);

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void notifyChat(String room, String sender) {

        var foundRoom = chatRoomRepository.findById(Long.parseLong(room));

        if(foundRoom.isEmpty()){
            return;
        }

        try{
            var friend = foundRoom.get().getFriend();
            SseEmitter emitter = null;

            if(friend.getFrom().getId() == Long.parseLong(sender)){
                emitter = emitters.get(friend.getTo().getId());

            } else {
                emitter = emitters.get(friend.getFrom().getId());
            }

            if(emitter != null){
                emitter.send(SseEmitter.event().name("chat").data(room));
            }
        } catch (IOException e){
            log.info(e.getMessage());
        }
    }
}
