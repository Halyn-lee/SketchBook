package com.app.sketchbook.friend.controller;

import com.app.sketchbook.friend.entity.Friend;
import com.app.sketchbook.friend.entity.FriendStatus;
import com.app.sketchbook.friend.service.FriendService;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserService userService;

    //친구 목록 보여주기
    @GetMapping("/list")
    public String friendList(Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        List<Friend> friends = friendService.getFriends(user);
        model.addAttribute("friends", friends);
        return "friend_list";
    }

    //친구 찾기
    @GetMapping("/search")
    public String friendSearch(@RequestParam String keyword, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        List<SketchUser> friends = friendService.searchFriends(user, keyword);
        model.addAttribute("friends", friends);
        return "friend_search";
    }

    //사용자 검색
    @GetMapping("/userSearch")
    public String userSearch(@RequestParam String keyword, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        Map<SketchUser, FriendStatus> users = friendService.searchAllUsers(user, keyword);
        model.addAttribute("users", users);
        return "user_search";
    }

    //친구 요청
    @PostMapping("/request")
    public String friendRequest(@RequestParam Long friendId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.requestFriend(user, friendId);
        model.addAttribute("message", message);
        return "redirect:/friend/list";
    }

    //친구 요청 취소
    @PostMapping("/cancel")
    public String friendRequestCancel(@RequestParam Long friendId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.cancelFriendRequest(user, friendId);
        model.addAttribute("message", message);
        return "redirect:/friend/list";
    }

    //친구 수락
    @PostMapping("/accept")
    public String friendRequestAccept(@RequestParam Long friendId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.acceptFriendRequest(user, friendId);
        model.addAttribute("message", message);
        return "redirect:/friend/list";
    }

    //친구 거절
    @PostMapping("/reject")
    public String friendRequestReject(@RequestParam Long friendId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.rejectFriendRequest(user, friendId);
        model.addAttribute("message", message);
        return "redirect:/friend/list";
    }

    //친구 삭제
    @PostMapping("/delete")
    public String friendDelete(@RequestParam Long friendId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.deleteFriend(user, friendId);
        model.addAttribute("message", message);
        return "redirect:/friend/list";
    }

    //사용자 차단
    @PostMapping("/block")
    public String blockUser(@RequestParam Long blockId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.blockUser(user, blockId);
        model.addAttribute("message", message);
        return "redirect:/friend/list";
    }

    //사용자 차단 해제
    @PostMapping("/unblock")
    public String unblockUser(@RequestParam Long blockId, Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        String message = friendService.unblockUser(user, blockId);
        model.addAttribute("message", message);
        return "redirect:/friend/list";
    }

    //프로필 보기
    @GetMapping("/profile/{profileOwnerId}")
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
}