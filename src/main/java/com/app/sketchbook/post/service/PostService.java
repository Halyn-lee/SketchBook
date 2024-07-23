package com.app.sketchbook.post.service;

import com.app.sketchbook.post.entity.Image;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.repository.ImageRepository;
import com.app.sketchbook.post.repository.PostRepository;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

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

    public Slice<Post> fetchPostsByPage(int pageNumber) {
//        CustomOAuth2User user = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        SketchUser userEntity = userRepository.findByUsername(user.getUsername());
        SketchUser userEntity = userRepository.getReferenceById(1L); // 임시
        PageRequest pageRequest = PageRequest.of(pageNumber, 3);
        Slice<Post> posts = postRepository.findBySketchUser(userEntity, pageRequest);
        return posts;
    }
}