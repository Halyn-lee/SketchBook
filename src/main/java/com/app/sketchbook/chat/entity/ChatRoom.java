package com.app.sketchbook.chat.entity;

import com.app.sketchbook.user.entity.SketchUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private SketchUser requester;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private SketchUser receiver;
}
