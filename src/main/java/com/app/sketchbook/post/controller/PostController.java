package com.app.sketchbook.post.controller;

import com.app.sketchbook.post.entity.Image;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.repository.PostRepository;
import com.app.sketchbook.post.service.PostService;
import com.app.sketchbook.reply.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;

    @GetMapping("/main")
    public String main(Model model) {
        return "main";
    }

    @GetMapping("/getpost/{pageNumber}")
    public String post_list(Model model, @PathVariable int pageNumber) {
        Slice<Post> posts = postService.fetchPostsByPage(pageNumber);
        if (posts.hasNext()) {
            model.addAttribute("nextPageNumber", pageNumber + 1);
        }
        model.addAttribute("postList", posts.getContent());
        return "post";
    }

    @PostMapping("/post/create")
    public String post_create(Post post, @RequestParam("imageData") String imageDataList) {
        // Post 엔티티에 저장
        Post savedPost = postService.post_create(post);

        List<Image> images = new ArrayList<>();
        String[] imageDataArray = imageDataList.split("base64,");

        for (int i = 1; i < imageDataArray.length; i++) {
            String imageData = imageDataArray[i]; // base64 데이터만 가져옴
            if (imageData != null && !imageData.isEmpty()) {
                try {
                    // 공백 및 모든 공백 문자 제거
                    imageData = imageData.replaceAll("\\s+", "");

                    // Base64 디코딩
                    byte[] imageBytes = Base64.getDecoder().decode(imageData);

                    // 파일 저장 경로 설정
                    String filePath = "C:/images/" + UUID.randomUUID() + ".png";
                    try (OutputStream stream = new FileOutputStream(filePath)) {
                        stream.write(imageBytes);
                    }

                    // Image 엔티티 생성 및 저장
                    Image image = new Image();
                    image.setFile_path(filePath);
                    image.setPost(savedPost);
                    images.add(image);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // Base64 디코딩 예외 발생 시 처리
                    e.printStackTrace();
                }
            }
        }

        postService.saveImages(images);
        return "redirect:/main";
    }
}