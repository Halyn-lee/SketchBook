package com.app.sketchbook.friend.service;

import com.app.sketchbook.friend.entity.Friend;
import com.app.sketchbook.friend.entity.FriendStatus;
import com.app.sketchbook.friend.repository.FriendRepository;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FriendService {
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    //친구 목록 가져오기
    public List<Friend> getFriends(Long userId) {
        SketchUser user = userRepository.findById(userId).orElseThrow();
        return friendRepository.findByFromOrToAndStatus(user, user, FriendStatus.ACCEPTED);
    }

    //친구 상태에 존재하는지 확인
    private Optional<Friend> checkFriend(SketchUser user, SketchUser friend) {
        return friendRepository.findByFromAndToOrFromAndTo(user, friend, friend, user);
    }

    //특정 친구 상태 여부 확인
    private boolean hasFriendStatus(SketchUser user, SketchUser friend, FriendStatus status) {
        return friendRepository.existsByFromAndToAndStatus(user, friend, status) || friendRepository.existsByFromAndToAndStatus(friend, user, status);
    }

    //친구 필터링
    private List<SketchUser> filterFriends(List<SketchUser> users, SketchUser user) {
        List<SketchUser> filteredUsers = new ArrayList<>();
        for (SketchUser filterUser : users) {
            Optional<Friend> existingStatus = checkFriend(user, filterUser);
            if (existingStatus.isPresent() && existingStatus.get().getStatus() == FriendStatus.ACCEPTED) {
                filteredUsers.add(filterUser);
            }
        }
        return filteredUsers;
    }

    //친구 찾기
    public List<SketchUser> searchFriends(Long userId, String keyword) {
        SketchUser user = userRepository.findById(userId).orElseThrow();
        List<SketchUser> usersByUsername = userRepository.findByUsernameContainingIgnoreCase(keyword);
        List<SketchUser> usersByEmail = userRepository.findByEmailContainingIgnoreCase(keyword);
        Set<SketchUser> uniqueUsers = new HashSet<>(usersByUsername);
        uniqueUsers.addAll(usersByEmail);
        return filterFriends(new ArrayList<>(uniqueUsers), user);
    }

    //사용자 검색
    public Map<SketchUser, FriendStatus> searchAllUsers(Long userId, String keyword) {
        List<SketchUser> usersByUsername = userRepository.findByUsernameContainingIgnoreCase(keyword);
        List<SketchUser> usersByEmail = userRepository.findByEmailContainingIgnoreCase(keyword);
        Set<SketchUser> uniqueUsers = new HashSet<>(usersByUsername);
        uniqueUsers.addAll(usersByEmail);

        Map<SketchUser, FriendStatus> result = new HashMap<>();
        SketchUser user = userRepository.findById(userId).orElseThrow();
        for (SketchUser sketchUser : uniqueUsers) {
            Optional<Friend> existingStatus = checkFriend(user, sketchUser);
            result.put(sketchUser, existingStatus.isPresent() ? existingStatus.get().getStatus() : FriendStatus.NOT_FRIEND);
        }
        return result;
    }

    //친구 요청
    @Transactional
    public String requestFriend(Long userId, Long friendId) {
        SketchUser user = userRepository.findById(userId).orElseThrow();
        if (friendId.equals(userId)) {
            return "나 자신은 영원한 인생의 친구입니다.";
        }
        SketchUser friend = userRepository.findById(friendId).orElseThrow();
        Optional<Friend> existingStatus = checkFriend(user, friend);
        if (existingStatus.isPresent()) {
            Friend friendStatus = existingStatus.get();
            if (friendStatus.getStatus() == FriendStatus.ACCEPTED) {
                return "이미 등록된 친구입니다.";
            } else if (friendStatus.getStatus() == FriendStatus.PENDING) {
                return "이미 요청 중인 상태입니다.";
            } else if (friendStatus.getStatus() == FriendStatus.REJECTED) {
                if(friendStatus.getFrom().equals(user)) {
                    friendStatus.setStatus(FriendStatus.PENDING);
                    friendRepository.save(friendStatus);
                    return "친구 요청을 다시 보냈습니다.";
                } else {
                    friendStatus.setFrom(user);
                    friendStatus.setTo(friend);
                    friendStatus.setStatus(FriendStatus.PENDING);
                    friendRepository.save(friendStatus);
                    return "친구 요청을 보냈습니다.";
                }
            } else if (friendStatus.getStatus() == FriendStatus.BLOCKED) {
                return "";
            } else if (friendStatus.getStatus() == FriendStatus.DELETED) {
                friendStatus.setFrom(user);
                friendStatus.setTo(friend);
                friendStatus.setStatus(FriendStatus.PENDING);
                friendRepository.save(friendStatus);
                return "친구 요청을 보냈습니다.";
            } else {
                return "";
            }
        } else {
            Friend newFriend = new Friend();
            newFriend.setFrom(user);
            newFriend.setTo(friend);
            newFriend.setStatus(FriendStatus.PENDING);
            friendRepository.save(newFriend);
            return "친구 요청을 보냈습니다.";
        }
    }

    //친구 요청 취소
    @Transactional
    public String cancelFriendRequest(Long userId, Long friendId) {
        SketchUser user = userRepository.findById(userId).orElseThrow();
        SketchUser friend = userRepository.findById(friendId).orElseThrow();
        Optional<Friend> pendingStatus = friendRepository.findByFromAndToAndStatus(user, friend, FriendStatus.PENDING);
        if(pendingStatus.isPresent()){
            Friend friendStatus = pendingStatus.get();
            friendRepository.delete(friendStatus);
            return "요청을 취소합니다.";
        }
        return "";
    }

    //친구 수락
    @Transactional
    public String acceptFriendRequest(Long userId, Long friendId) {
        SketchUser user = userRepository.findById(userId).orElseThrow();
        SketchUser friend = userRepository.findById(friendId).orElseThrow();
        Optional<Friend> pendingStatus = friendRepository.findByFromAndToAndStatus(friend, user, FriendStatus.PENDING);
        if (pendingStatus.isPresent()) {
            Friend friendStatus = pendingStatus.get();
            friendStatus.setStatus(FriendStatus.ACCEPTED);
            friendRepository.save(friendStatus);
            return "친구 요청을 수락하였습니다.";
        }
        return "새로운 친구 요청이 없습니다.";
    }

    //친구 거절
    @Transactional
    public String rejectFriendRequest(Long userId, Long friendId) {
        SketchUser user = userRepository.findById(userId).orElseThrow();
        SketchUser friend = userRepository.findById(friendId).orElseThrow();
        Optional<Friend> pendingStatus = friendRepository.findByFromAndToAndStatus(friend, user, FriendStatus.PENDING);
        if(pendingStatus.isPresent()){
            Friend friendStatus = pendingStatus.get();
            friendStatus.setStatus(FriendStatus.REJECTED);
            friendRepository.save(friendStatus);
            return "친구 요청을 거절하였습니다.";
        }
        return "";
    }

    //친구 삭제
    @Transactional
    public String deleteFriend(Long userId, Long friendId) {
        SketchUser user = userRepository.findById(userId).orElseThrow();
        SketchUser friend = userRepository.findById(friendId).orElseThrow();
        Optional<Friend> existingStatus = checkFriend(user, friend);
        if(existingStatus.isPresent()){
            Friend friendStatus = existingStatus.get();
            friendStatus.setStatus(FriendStatus.DELETED);
            friendRepository.save(friendStatus);
            return "친구를 삭제하였습니다.";
        }
        return "";
    }

    //사용자 차단
    @Transactional
    public String blockUser(Long userId, Long blockId) {
        SketchUser user = userRepository.findById(userId).orElseThrow();
        SketchUser blacklist = userRepository.findById(blockId).orElseThrow();
        Optional<Friend> existingStatus = checkFriend(user, blacklist);
        if(existingStatus.isPresent()){
            Friend friendStatus = existingStatus.get();
            friendStatus.setStatus(FriendStatus.BLOCKED);
            friendRepository.save(friendStatus);
            return "친구를 차단하였습니다.";
        }
        Friend friendStatus = new Friend();
        friendStatus.setFrom(user);
        friendStatus.setTo(blacklist);
        friendStatus.setStatus(FriendStatus.BLOCKED);
        friendRepository.save(friendStatus);
        return "사용자를 차단하였습니다.";
    }

    //사용자 차단 해제
    @Transactional
    public String unblockUser(Long userId, Long blockId) {
        SketchUser user = userRepository.findById(userId).orElseThrow();
        SketchUser blacklist = userRepository.findById(blockId).orElseThrow();
        Optional<Friend> existingStatus = checkFriend(user, blacklist);
        if(existingStatus.isPresent() && existingStatus.get().getStatus()==FriendStatus.BLOCKED){
            Friend friendStatus = existingStatus.get();
            friendRepository.delete(friendStatus);
            return "사용자 차단을 해제하였습니다.";
        }
        return "";
    }

    //프로필 보기
    public SketchUser getUserProfile(Long userId, Long profileOwnerId) {
        SketchUser user = userRepository.findById(userId).orElseThrow();
        SketchUser profileOwner = userRepository.findById(profileOwnerId).orElseThrow();
        if(hasFriendStatus(user, profileOwner, FriendStatus.BLOCKED)){
            throw new AccessDeniedException("You are not allowed to view this profile.");
        }
        if(!profileOwner.isProfile_public() && !hasFriendStatus(user, profileOwner, FriendStatus.ACCEPTED)){
            throw new AccessDeniedException("You are not allowed to view this profile.");
        }
        return profileOwner;
    }

    public Optional<Friend> findById(Long friendNo) {
        return friendRepository.findById(friendNo);
    }
}