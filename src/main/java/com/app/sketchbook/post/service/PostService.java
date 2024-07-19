package com.app.sketchbook.post.service;

import com.app.sketchbook.post.entity.Image;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.repository.ImageRepository;
import com.app.sketchbook.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;

    public Post post_create(Post post) {
        post.set_deleted(false);
        post.setCreated_date(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void saveImage(Image image) {
        imageRepository.save(image);
    }
}