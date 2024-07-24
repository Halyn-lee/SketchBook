package com.app.sketchbook.post.controller;

import com.app.sketchbook.post.entity.Image;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

@Log4j2
@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/main")
    public String main(HttpSession session, Model model, Post post) {
        String username = session.getAttribute("username").toString();
        log.info(username);
        //System.out.print(username);
        model.addAttribute("post", new Post()); // 임시
        return "main";
    }

    @PostMapping("/post/create")
    public String post_create(Post post, @RequestParam("imageData") String imageData) {
        // Post 엔티티에 저장
        Post savedPost = postService.post_create(post);

        if (imageData != null && !imageData.isEmpty()) {
            try {
                // 이미지 데이터 처리
                String base64Image = imageData.split(",")[1];
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                // 파일 저장 경로 설정
                String filePath = "C:/images/" + System.currentTimeMillis() + ".png";
                try (OutputStream stream = new FileOutputStream(filePath)) {
                    stream.write(imageBytes);
                }

                // Image 엔티티 생성 및 저장
                Image image = new Image();
                image.setFile_path(filePath);
                image.setPost(savedPost);
                postService.saveImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "redirect:/main";
    }
}