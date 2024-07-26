package com.app.sketchbook.chat.repository;

import com.app.sketchbook.chat.entity.ChatLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ChatRepository extends MongoRepository<ChatLog,String> {
    public List<ChatLog> findAllByRoomAndSendTimeAfterOrderBySendTimeAsc(String room, Date startDate);
}
