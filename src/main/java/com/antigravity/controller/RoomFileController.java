package com.antigravity.controller;

import com.antigravity.dto.CreateFileRequest;
import com.antigravity.dto.FileDto;
import com.antigravity.service.RoomFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms/{roomCode}/files")
@RequiredArgsConstructor
public class RoomFileController {

    private final RoomFileService fileService;

    @GetMapping
    public ResponseEntity<List<FileDto>> getRoomFiles(@PathVariable String roomCode) {
        return ResponseEntity.ok(fileService.getFilesByRoomCode(roomCode));
    }

    // Anyone can create new files
    @PostMapping
    public ResponseEntity<FileDto> createFile(@PathVariable String roomCode, 
                                            @Valid @RequestBody CreateFileRequest request) {
        return ResponseEntity.ok(fileService.createFile(roomCode, request));
    }

    // Anyone can delete files
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable String roomCode, 
                                         @PathVariable Long fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.ok().build();
    }

    // Anyone can rename files
    @PutMapping("/{fileId}/rename")
    public ResponseEntity<FileDto> renameFile(@PathVariable String roomCode,
                                            @PathVariable Long fileId,
                                            @RequestBody String newName) {
        return ResponseEntity.ok(fileService.renameFile(fileId, newName));
    }

    // Anyone can update file content (collaboration)
    @PutMapping("/{fileId}/content")
    public ResponseEntity<Void> updateFileContent(@PathVariable String roomCode,
                                                @PathVariable Long fileId,
                                                @RequestBody String content) {
        fileService.updateFileContent(fileId, content);
        return ResponseEntity.ok().build();
    }
}
