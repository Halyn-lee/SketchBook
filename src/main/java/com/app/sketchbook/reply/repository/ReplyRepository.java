package com.app.sketchbook.reply.repository;

import com.app.sketchbook.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {

}