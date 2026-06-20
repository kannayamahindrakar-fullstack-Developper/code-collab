package com.antigravity.service;

import com.antigravity.dto.ChatDto;
import com.antigravity.entity.ChatMessage;
import com.antigravity.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatDto saveMessage(String roomCode, String senderUsername, String receiverUsername, String content) {
        ChatMessage message = ChatMessage.builder()
                .roomCode(roomCode)
                .senderUsername(senderUsername)
                .receiverUsername(receiverUsername)
                .content(content)
                .build();

        ChatMessage saved = chatMessageRepository.save(message);
        return mapToDto(saved);
    }

    public List<ChatDto> getChatHistory(String roomCode, String user1, String user2) {
        List<ChatMessage> messages = chatMessageRepository.findChatHistory(roomCode, user1, user2);
        return messages.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteRoomChatHistory(String roomCode) {
        chatMessageRepository.deleteByRoomCode(roomCode);
    }

    private ChatDto mapToDto(ChatMessage msg) {
        return ChatDto.builder()
                .id(msg.getId())
                .roomCode(msg.getRoomCode())
                .senderUsername(msg.getSenderUsername())
                .receiverUsername(msg.getReceiverUsername())
                .content(msg.getContent())
                .timestamp(msg.getTimestamp())
                .build();
    }
}
