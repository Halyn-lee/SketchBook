package com.app.sketchbook.chat.dto;

import com.app.sketchbook.user.entity.SketchUser;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatRoomModel {
    private SketchUser opponent;
    private boolean messagesExists;
}
