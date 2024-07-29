package com.app.sketchbook.chat.service;

import com.app.sketchbook.chat.dto.ChatRoomModel;
import com.app.sketchbook.chat.entity.ChatRoom;

import java.util.Date;
import java.util.List;

public interface ChatRoomService {

    void createRoom(Long friendNo);
    boolean checkRoomCreated(Long roomNo);
    void updateDisconnectTime(Long room, Long user);
    void updateLastSend(Long room, Date time);
    List<ChatRoomModel> getChatRoomList(Long user);
}
