package com.antigravity.controller;

import com.antigravity.dto.ChatDto;
import com.antigravity.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.corundumstudio.socketio.SocketIOServer;

import java.util.List;

@RestController
@RequestMapping("/api/rooms/{roomCode}/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SocketIOServer socketIOServer;

    @GetMapping("/history/{targetUsername}")
    public ResponseEntity<List<ChatDto>> getChatHistory(
            @PathVariable String roomCode,
            @PathVariable String targetUsername,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<ChatDto> history = chatService.getChatHistory(roomCode, userDetails.getUsername(), targetUsername);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> clearRoomChatHistory(@PathVariable String roomCode) {
        chatService.deleteRoomChatHistory(roomCode);
        
        // Notify all users in the room to clear their chat UI
        socketIOServer.getRoomOperations(roomCode).sendEvent("chat_cleared");
        
        return ResponseEntity.noContent().build();
    }
}
