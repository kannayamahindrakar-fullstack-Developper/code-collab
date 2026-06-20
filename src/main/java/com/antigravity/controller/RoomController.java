package com.antigravity.controller;

import com.antigravity.dto.RoomDto;
import com.antigravity.dto.RoomRequest;
import com.antigravity.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.corundumstudio.socketio.SocketIOServer;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoomController {

    private final RoomService roomService;
    private final SocketIOServer socketIOServer;

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(
            @Valid @RequestBody RoomRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        RoomDto created = roomService.createRoom(request, userDetails.getUsername());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/join/{roomCode}")
    public ResponseEntity<RoomDto> joinRoom(
            @PathVariable String roomCode,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        RoomDto joined = roomService.joinRoomByCode(roomCode, userDetails.getUsername());
        return ResponseEntity.ok(joined);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getUserRooms(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<RoomDto> rooms = roomService.getUserRooms(userDetails.getUsername());
        return ResponseEntity.ok(rooms);
    }

    @DeleteMapping("/{roomId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String roomCode = roomService.deleteRoom(roomId, userDetails.getUsername());
        
        // Broadcast event to kick everyone out of the deleted room
        socketIOServer.getRoomOperations(roomCode).sendEvent("room_deleted");
        
        return ResponseEntity.noContent().build();
    }
}
