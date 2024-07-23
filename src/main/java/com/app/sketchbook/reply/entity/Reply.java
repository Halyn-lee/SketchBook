package com.app.sketchbook.reply.entity;

import com.app.sketchbook.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(length = 4000)
    private String content;

    private LocalDateTime created_date;

    private LocalDateTime modified_date;

    private boolean is_deleted;

    @ManyToOne
    private Post post;
}