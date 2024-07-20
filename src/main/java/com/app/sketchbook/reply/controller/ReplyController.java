package com.app.sketchbook.reply.controller;

import com.app.sketchbook.reply.entity.Reply;
import com.app.sketchbook.reply.service.ReplyService;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/reply")
@RequiredArgsConstructor
@Controller
public class ReplyController {
    private final PostService postService;
    private final ReplyService replyService;

    @PostMapping("/create/{no}")
    public String create_reply(Model model, @PathVariable("no") Integer no, String content) {
        Post post = postService.getPost(no);
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
}