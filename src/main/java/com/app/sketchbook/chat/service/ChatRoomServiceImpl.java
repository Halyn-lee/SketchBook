package com.app.sketchbook.chat.service;

import com.app.sketchbook.chat.dto.ChatRoomModel;
import com.app.sketchbook.chat.entity.ChatRoom;
import com.app.sketchbook.chat.repository.ChatRoomRepository;
import com.app.sketchbook.friend.repository.FriendRepository;
import com.app.sketchbook.friend.service.FriendService;
import com.app.sketchbook.user.repository.UserRepository;
import com.app.sketchbook.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService{

    private final ChatRoomRepository chatRoomRepository;
    private final FriendService friendService;
    private final UserService userService;

    @Transactional
    @Override
    public void createRoom(Long friendNo) {
        var room = new ChatRoom();
        var friend = friendService.findById(friendNo);

        if(friend.isEmpty()){
            return;
        }

        room.setId(friendNo);
        room.setFriend(friend.get());
        chatRoomRepository.save(room);
    }

    @Override
    public boolean checkRoomCreated(Long roomNo) {
        return chatRoomRepository.existsById(roomNo);
    }

    @Transactional
    @Override
    public void updateDisconnectTime(Long room, Authentication auth) {
        var foundRoom = chatRoomRepository.findById(room);

        if(foundRoom.isEmpty()){
            log.info("Empty Room");
            return;
        }

        var friend = foundRoom.get().getFriend();
        var user = userService.principalUser(auth);

        log.info("User ID: "+user.getId());

        if(friend.getFrom().getId().equals(user.getId())){
            foundRoom.get().setFromDisconnection(new Date());
        } else {
            foundRoom.get().setToDisconnection(new Date());
        }
    }

    @Transactional
    @Override
    public void updateLastSend(Long room, Date time) {
        var foundRoom = chatRoomRepository.findById(room);

        if(foundRoom.isEmpty()) {
            return;
        }

        foundRoom.get().setLastSend(time);
    }

    @Override
    public List<ChatRoomModel> getChatRoomList() {

        var user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());

        if(user == null){
            return List.of();
        }

        var allRooms = friendService.getFriends(user);
        var receivedRooms = chatRoomRepository.findAllByIdWithExistsMessage(user);

        List<ChatRoomModel> result = new ArrayList<>();

        for(var room : allRooms){
            ChatRoomModel model = new ChatRoomModel();

            model.setRoom(room.getNo());

            if(room.getFrom().getId().equals(user.getId())){
                model.setOpponent(room.getTo().getUsername());
            } else {
                model.setOpponent(room.getFrom().getUsername());
            }

            if(receivedRooms.contains(room.getNo())){
                model.setMessagesExists(true);
            }

            result.add(model);
        }

        return result;
    }
}
