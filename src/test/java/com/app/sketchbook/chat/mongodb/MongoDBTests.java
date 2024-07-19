package com.app.sketchbook.chat.mongodb;

import com.app.sketchbook.chat.dto.ChatLog;
import com.app.sketchbook.chat.dto.ReceivedChat;
import com.app.sketchbook.chat.service.ChatLogService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log
@SpringBootTest
public class MongoDBTests {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ChatLogService chatLogService;

    @Test
    public void testConnection(){
        try {
            String ok = mongoTemplate.getDb().getName();
            System.out.println("MongoDB connection established: " + ok);
        } catch (Exception e) {
            System.out.println("MongoDB connection failed: " + e.getMessage());
        }
    }

    @Test
    public void testInsertChatLog(){
        ReceivedChat chatLog = new ReceivedChat();
        chatLog.setRoom("1234");
        chatLog.setUser("test");
        chatLog.setSendTime(new Date());
        chatLog.setContent("my test");
        chatLogService.insertChatLog(chatLog);
    }

    @Test
    public void testGetRecentChats(){
        var list = chatLogService.getRecentLogs("1234");
        log.info("List size : "+list.size());
        assertFalse(list.isEmpty());
    }

}
