package com.app.sketchbook.chat.service;

import com.app.sketchbook.chat.entity.ChatLog;
import com.app.sketchbook.chat.dto.ReceivedChat;
import com.app.sketchbook.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatLogServiceImpl implements ChatLogService {
    private final ChatRepository chatRepository;

    @Override
    public void insertChatLog(ReceivedChat chat) {
        ChatLog chatLog = new ChatLog(chat);
        chatRepository.save(chatLog);
    }

    @Override
    public List<ChatLog> getRecentLogs(String room) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date startDate = cal.getTime();
        return chatRepository.findAllByRoomAndSendTimeAfterOrderBySendTimeAsc(room, startDate);
    }
}
