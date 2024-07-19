package com.app.sketchbook.reply.controller;

import com.app.sketchbook.reply.service.ReplyService;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/reply")
@RequiredArgsConstructor
@Controller
public class ReplyController {
    private final PostService postService;
    private final ReplyService replyService;

//    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{no}")
    public String create_reply(Model model, @PathVariable("no") Integer no, String content) {
        Post post = postService.getPost(no);
        replyService.reply_create(post, content);

        model.addAttribute("post", post);

        return "redirect:/main";
    }
}