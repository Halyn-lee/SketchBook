package com.app.sketchbook.reply.controller;

import com.app.sketchbook.reply.entity.Reply;
import com.app.sketchbook.reply.service.ReplyService;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.service.PostService;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/reply")
@RequiredArgsConstructor
@Controller
public class ReplyController {
    private final PostService postService;
    private final ReplyService replyService;
    private final UserRepository userRepository;

    @PostMapping("/create/{no}")
    public String create_reply(Model model, @PathVariable("no") Integer no, String content) {
        Post post = null;

        try {
            post = postService.getPost(no);
        } catch (Exception e) {
            return "error"; // 나중에 예외처리용
        }

        replyService.reply_create(post, content);

        model.addAttribute("post", post);

        return "redirect:/main";
    }

    @PostMapping("/modify/{no}")
    public ResponseEntity<?> modify_reply(@PathVariable("no") Integer no, @RequestBody Reply modifiedReply) {
        Reply reply = replyService.getReply(no);
        if (reply != null) {
            reply.setContent(modifiedReply.getContent());
            replyService.reply_modify(reply);
            return ResponseEntity.ok().body("{\"success\": true}");
        }
        return ResponseEntity.status(400).body("{\"success\": false}");
    }

    @PostMapping("/delete/{no}")
    public ResponseEntity<?> delete_reply(@PathVariable("no") Integer no) {
        Reply reply = replyService.getReply(no);
        if (reply != null) {
            replyService.reply_delete(reply);
            return ResponseEntity.ok().body("{\"success\": true}");
        }
        return ResponseEntity.status(400).body("{\"success\": false}");
    }

    @PostMapping("/like/{no}")
    public ResponseEntity<?> like_reply(@PathVariable Long no, SketchUser user) {
        // 세션의 username 가져와서 유저엔티티에서 사용자 get
        // CustomOAuth2User user = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userRepository.getReferenceById(1L); //임시

        if (no != null) {
            replyService.like_reply(no, user);
            return ResponseEntity.ok().body("{\"success\": true}");
        }
        return ResponseEntity.status(400).body("{\"success\": false}");
    }
}