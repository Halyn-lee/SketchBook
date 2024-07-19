package com.app.sketchbook.post.service;

import com.app.sketchbook.post.entity.Image;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.repository.ImageRepository;
import com.app.sketchbook.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public void saveImages(List<Image> images) {
        imageRepository.saveAll(images);
    }

    public Post getPost(Integer no) {
        Optional<Post> post = this.postRepository.findById(no);
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new RuntimeException("해당 게시글 찾을 수 없음");
        }
    }
}