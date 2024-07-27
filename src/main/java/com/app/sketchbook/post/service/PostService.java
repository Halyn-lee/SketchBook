package com.app.sketchbook.post.service;

import com.app.sketchbook.friend.entity.Friend;
import com.app.sketchbook.friend.entity.FriendStatus;
import com.app.sketchbook.friend.repository.FriendRepository;
import com.app.sketchbook.post.entity.Image;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.repository.ImageRepository;
import com.app.sketchbook.post.repository.PostRepository;
import com.app.sketchbook.reply.entity.Reply;
import com.app.sketchbook.reply.repository.ReplyRepository;
import com.app.sketchbook.reply.service.ReplyService;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.UserRepository;
import com.app.sketchbook.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final ReplyRepository replyRepository;
    private final FriendRepository friendRepository;
    private final ReplyService replyService;
    private final UserService userService;

    public Post create_post(Post post, SketchUser user) {
        post.setSketchUser(user);
        post.setCreated_date(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void saveImages(List<Image> images) {
        imageRepository.saveAll(images);
    }

    public Post getPost(Integer no) {
        Optional<Post> post = this.postRepository.findById(no);
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new RuntimeException("해당 게시글 찾을 수 없음");
        }
    }

    // 마이 페이지 - 내 게시물만 출력
    public Slice<Post> fetchPostsByPage(int pageNumber) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        PageRequest pageRequest = PageRequest.of(pageNumber, 3);
        Slice<Post> posts = postRepository.findBySketchUserId(user.getId(), pageRequest);
        return posts;
    }

    // 메인 페이지 - 내 게시물 및 친구 게시물 출력
    public Slice<Post> fetchPostsByPageAndFriendStatus(int pageNumber) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());

        // 로그인한 사용자와 친구인 사용자들 조회
        List<Friend> friendsFrom = friendRepository.findByFromOrToAndStatus(user, user, FriendStatus.ACCEPTED);

        Set<SketchUser> friendUsers = new HashSet<>();
        for (Friend friend : friendsFrom) {
            if (friend.getFrom().equals(user)) {
                friendUsers.add(friend.getTo());
            } else if (friend.getTo().equals(user)) {
                friendUsers.add(friend.getFrom());
            }
        }

        // 로그인한 사용자 자신의 게시물 추가
        friendUsers.add(user);

        // 로그인한 사용자와 친구인 사용자들의 게시물 조회
        PageRequest pageRequest = PageRequest.of(pageNumber, 3);
        Slice<Post> posts = postRepository.findBySketchUserIn(friendUsers, pageRequest);

        return posts;
    }

    @Transactional
    public void delete_post(Long no) {
        Post post = postRepository.getReferenceById(no.intValue()); // 범위에 대한 예외처리 필요
        List<Reply> replyList = replyRepository.findByPostNo(post.getNo());
        for (Reply reply : replyList) {
            replyService.reply_delete(reply);
        }
        post.set_deleted(true);
        postRepository.save(post);
    }

    public Post modify_post(Long no, String content) {
        Post post = postRepository.getReferenceById(no.intValue());
        post.setContent(content);
        post.setModified_date(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void like_post(Long no, SketchUser user) { // 임시
        Post post = postRepository.getReferenceById(no.intValue()); // 범위에 대한 예외처리 필요
        post.getLike().add(user);
        postRepository.save(post);
    }

    public void cancel_post_like(Long no, SketchUser user) {
        Post post = postRepository.getReferenceById(no.intValue());
        Set<SketchUser> likedUser = post.getLike();
        likedUser.remove(user);
        post.setLike(likedUser);
        postRepository.save(post);
    }
}