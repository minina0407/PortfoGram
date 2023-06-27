package com.api.PortfoGram.chat.service;

import com.api.PortfoGram.chat.entity.ChatRoomEntity;
import com.api.PortfoGram.chat.entity.UserChatRoomEntity;
import com.api.PortfoGram.chat.repository.ChatRoomRepository;
import com.api.PortfoGram.chat.repository.UserChatRoomRepository;
import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final RabbitTemplate rabbitTemplate;
    public static final String CHAT_ROOM_EXCHANGE = "chat.room.exchange";
    public static final String CHAT_ROOM_JOIN = "chat.room.join";
    public static final String CHAT_ROOM_CREATE = "chat.room.create";

    @Transactional
    public Long createNewChatRoom(UserEntity sender, UserEntity receiver) {
        validateParameters(sender, receiver);
        // ChatRoom 생성 및 저장
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .build();
        chatRoomRepository.save(chatRoom);

        // UserChatRoom 생성 및 저장
        UserChatRoomEntity senderUserChatRoom = UserChatRoomEntity.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .build();
        userChatRoomRepository.save(senderUserChatRoom);

        UserChatRoomEntity receiverUserChatRoom = UserChatRoomEntity.builder()
                .chatRoom(chatRoom)
                .user(receiver)
                .build();
        userChatRoomRepository.save(receiverUserChatRoom);

        // RabbitMQ를 통해 채팅방 생성 메시지를 전송
        rabbitTemplate.convertAndSend(CHAT_ROOM_EXCHANGE , CHAT_ROOM_CREATE, chatRoom.getId());

        return chatRoom.getId();
    }

    public void joinChatRoom(Long roomId, UserEntity user) {
        // ChatRoom 조회 및 사용자 추가
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"채팅방을 찾을 수 없습니다"));

        UserChatRoomEntity userChatRoom = UserChatRoomEntity.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        userChatRoomRepository.save(userChatRoom);

        // RabbitMQ를 통해 채팅방 입장 메시지를 전송
        rabbitTemplate.convertAndSend(CHAT_ROOM_EXCHANGE , CHAT_ROOM_JOIN , roomId);
    }

    public ChatRoomEntity getChatRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException(ExceptionEnum.RESPONSE_NOT_FOUND,"채팅방을 찾을 수 없습니다"));
    }
    private void validateParameters(UserEntity sender, UserEntity receiver) {
        if (sender == null || receiver == null) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID);
        }

        Optional<ChatRoomEntity> existingChatRoom = chatRoomRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId());
        if (existingChatRoom.isPresent()) {
            throw new BadRequestException(ExceptionEnum.REQUEST_PARAMETER_INVALID,"이미 존재하는 채팅방입니다.");
        }
    }
}
