package com.antigravity.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatDto {
    private Long id;
    private String roomCode;
    private String senderUsername;
    private String receiverUsername;
    private String content;
    private LocalDateTime timestamp;
}
