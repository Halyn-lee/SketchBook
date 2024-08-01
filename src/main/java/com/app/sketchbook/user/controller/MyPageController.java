package com.app.sketchbook.user.controller;

import com.app.sketchbook.friend.service.FriendService;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.service.FileStorageService;
import com.app.sketchbook.user.service.MyPageService;
import com.app.sketchbook.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class MyPageController {

    private final UserService userService;
    private final MyPageService myPageService;
    private final FriendService friendService;
    private final FileStorageService fileStorageService;

    //프로필 보기
    @GetMapping("/{profileOwnerId}")
    public String userProfile(@PathVariable Long profileOwnerId, Model model) {
        try {
            SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
            SketchUser profileOwner = friendService.getUserProfile(user, profileOwnerId);
            model.addAttribute("profileOwner", profileOwner);
            return "profile";
        } catch (AccessDeniedException e) {
            model.addAttribute("error", e.getMessage());
            return "access_denied";
        }
    }

    //프로필 사진 업로드
    @PostMapping("/uploadProfile")
    public String profileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String fileUrl = fileStorageService.storeFile(file, user.getId(), "profile");
        myPageService.uploadProfile(user, fileUrl);
        redirectAttributes.addFlashAttribute("message", "프로필 사진이 성공적으로 업데이트되었습니다.");
        return "redirect:/profile/" + user.getId();
    }

    //배경 사진 업로드
    @PostMapping("/uploadCover")
    public String coverUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String fileUrl = fileStorageService.storeFile(file, user.getId(), "cover");
        myPageService.uploadCover(user, fileUrl);
        redirectAttributes.addFlashAttribute("message", "배경 사진이 성공적으로 업데이트되었습니다.");
        return "redirect:/profile/" + user.getId();
    }
}
