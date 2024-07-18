package com.app.sketchbook.post.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(length = 4000)
    private String content;

    private LocalDateTime created_date;

    private LocalDateTime modified_date;

    private boolean is_deleted;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> image_list = new ArrayList<>();

//    @ManyToOne
//    private Member id; 회원기능 연동 후 추가할 것
}
