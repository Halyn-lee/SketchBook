package com.app.sketchbook.friend.controller;

import com.app.sketchbook.friend.entity.Friend;
import com.app.sketchbook.friend.entity.FriendStatus;
import com.app.sketchbook.friend.service.FriendService;
import com.app.sketchbook.user.entity.SketchUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;

    //친구 목록 보여주기
    @GetMapping("/list/{userId}")
    public String friendList(@PathVariable Long userId, Model model) {
        List<Friend> friends = friendService.getFriends(userId);
        model.addAttribute("friends", friends);
        return "friend_list";
    }

    //친구 찾기
    @GetMapping("/search")
    public String friendSearch(@RequestParam Long userId, @RequestParam String keyword, Model model) {
        List<SketchUser> friends = friendService.searchFriends(userId, keyword);
        model.addAttribute("friends", friends);
        return "friend_search";
    }

    //사용자 검색
    @GetMapping("/userSearch")
    public String userSearch(@RequestParam Long userId, @RequestParam String keyword, Model model) {
        Map<SketchUser, FriendStatus> users = friendService.searchAllUsers(userId, keyword);
        model.addAttribute("users", users);
        model.addAttribute("userId", userId);
        return "user_search";
    }

    //친구 요청
    @PostMapping("/request")
    public String friendRequest(@RequestParam Long userId, @RequestParam Long friendId, Model model) {
        String message = friendService.requestFriend(userId, friendId);
        model.addAttribute("message", message);
        return "redirect:/friends/list/" + userId;
    }

    //친구 요청 취소
    @PostMapping("/cancel")
    public String friendRequestCancel(@RequestParam Long userId, @RequestParam Long friendId, Model model) {
        String message = friendService.cancelFriendRequest(userId, friendId);
        model.addAttribute("message", message);
        return "redirect:/friends/list/" + userId;
    }

    //친구 수락
    @PostMapping("/accept")
    public String friendRequestAccept(@RequestParam Long userId, @RequestParam Long friendId, Model model) {
        String message = friendService.acceptFriendRequest(userId, friendId);
        model.addAttribute("message", message);
        return "redirect:/friends/list/" + userId;
    }

    //친구 거절
    @PostMapping("/reject")
    public String friendRequestReject(@RequestParam Long userId, @RequestParam Long friendId, Model model) {
        String message = friendService.rejectFriendRequest(userId, friendId);
        model.addAttribute("message", message);
        return "redirect:/friends/list/" + userId;
    }

    //친구 삭제
    @PostMapping("/delete")
    public String friendDelete(@RequestParam Long userId, @RequestParam Long friendId, Model model) {
        String message = friendService.deleteFriend(userId, friendId);
        model.addAttribute("message", message);
        return "redirect:/friends/list/" + userId;
    }

    //사용자 차단
    @PostMapping("/block")
    public String blockUser(@RequestParam Long userId, @RequestParam Long blockId, Model model) {
        String message = friendService.blockUser(userId, blockId);
        model.addAttribute("message", message);
        return "redirect:/friends/list/" + userId;
    }

    //사용자 차단 해제
    @PostMapping("/unblock")
    public String unblockUser(@RequestParam Long userId, @RequestParam Long blockId, Model model) {
        String message = friendService.unblockUser(userId, blockId);
        model.addAttribute("message", message);
        return "redirect:/friends/list/" + userId;
    }

    //프로필 보기
    @GetMapping("/profile/{userId}/{profileOwnerId}")
    public String UserProfile(@PathVariable Long userId, @PathVariable Long profileOwnerId, Model model) {
        try {
            SketchUser profileOwner = friendService.getUserProfile(userId, profileOwnerId);
            model.addAttribute("profileOwner", profileOwner);
            return "profile_view";
        } catch (AccessDeniedException e) {
            model.addAttribute("error", e.getMessage());
            return "access_denied";
        }
    }
}