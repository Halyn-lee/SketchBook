package com.app.sketchbook.reply.service;

import com.app.sketchbook.reply.entity.Reply;
import com.app.sketchbook.reply.repository.ReplyRepository;
import com.app.sketchbook.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;

    public void reply_create(Post post, String content) {
        Reply reply = new Reply();
        reply.setContent(content);
        reply.set_deleted(false);
        reply.setCreated_date(LocalDateTime.now());
        reply.setPost(post);
        replyRepository.save(reply);
    }
}