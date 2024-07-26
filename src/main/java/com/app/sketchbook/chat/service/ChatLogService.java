package com.app.sketchbook.chat.service;

import com.app.sketchbook.chat.entity.ChatLog;
import com.app.sketchbook.chat.dto.ReceivedChat;

import java.util.List;

public interface ChatLogService {
    public void insertChatLog(ReceivedChat chat);
    public List<ChatLog> getRecentLogs(String room);
    public void cacheChatLog(ReceivedChat chat);
}
