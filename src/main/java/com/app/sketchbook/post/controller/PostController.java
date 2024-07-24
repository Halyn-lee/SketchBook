package com.app.sketchbook.post.controller;

import com.app.sketchbook.post.DTO.ImageRequestDTO;
import com.app.sketchbook.post.entity.Image;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.repository.ImageRepository;
import com.app.sketchbook.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ImageRepository imageRepository;

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
    public String create_post(Post post, @RequestParam("imageData") String imageDataList) {
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

    @PostMapping("/post/delete/{no}")
    public ResponseEntity<?> delete_post(@PathVariable Long no) {
        if (no != null) {
            postService.post_delete(no);
            return ResponseEntity.ok().body("{\"success\": true}");
        }
        return ResponseEntity.status(400).body("{\"success\": false}");
    }

    @PostMapping("/post/modify/{no}")
    public String modify_post(@PathVariable Long no, Post post) {
        if (no != null) {
            return "redirect:/main";
        }
        return "redirect:/main";
    }

    // 이미지 리스트만 삭제
    @PostMapping("/images/delete")
    public ResponseEntity<String> deleteImages(@RequestBody ImageRequestDTO imageRequestDTO) {
        List<Long> selectedImageIds = imageRequestDTO.getSelectedImageIds();

        if (selectedImageIds != null && !selectedImageIds.isEmpty()) {
            selectedImageIds.forEach(id -> {
                Optional<Image> optionalImage = imageRepository.findById(id);
                optionalImage.ifPresent(image -> {
                    image.set_deleted(true);
                    imageRepository.save(image);
                });
            });

            return ResponseEntity.ok().body("{\"success\": true}");
        } else {
            return ResponseEntity.status(400).body("{\"success\": false}");
        }
    }
}