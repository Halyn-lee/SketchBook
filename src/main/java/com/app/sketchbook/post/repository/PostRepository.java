package com.app.sketchbook.post.repository;

import com.app.sketchbook.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {

}