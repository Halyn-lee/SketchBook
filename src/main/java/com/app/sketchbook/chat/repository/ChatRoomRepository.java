package com.app.sketchbook.chat.repository;

import com.app.sketchbook.chat.entity.ChatRoom;
import com.app.sketchbook.user.entity.SketchUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT C.id FROM ChatRoom C, Friend F WHERE C.id = F.no AND (F.from = :id OR F.to = :id) AND (C.fromDisconnection < C.lastSend OR C.toDisconnection < C.lastSend)")
    public List<Long> findAllByIdWithExistsMessage(@Param(value = "id") SketchUser id);
}
