package com.antigravity.service;

import com.antigravity.dto.CreateFileRequest;
import com.antigravity.dto.FileDto;
import com.antigravity.entity.Room;
import com.antigravity.entity.RoomFile;
import com.antigravity.repository.RoomFileRepository;
import com.antigravity.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomFileService {

    private final RoomFileRepository fileRepository;
    private final RoomRepository roomRepository;

    public List<FileDto> getFilesByRoomCode(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        return fileRepository.findByRoomId(room.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public FileDto createFile(String roomCode, CreateFileRequest request) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        RoomFile file = RoomFile.builder()
                .room(room)
                .fileName(request.getFileName())
                .language(request.getLanguage() != null ? request.getLanguage() : "javascript")
                .content(request.getIsDirectory() != null && request.getIsDirectory() ? "" : generateBoilerplate(request.getFileName()))
                .isDirectory(request.getIsDirectory() != null ? request.getIsDirectory() : false)
                .parentId(request.getParentId())
                .build();

        RoomFile saved = fileRepository.save(file);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteFile(Long fileId) {
        if (!fileRepository.existsById(fileId)) {
            throw new RuntimeException("File not found");
        }
        fileRepository.deleteById(fileId);
    }

    @Transactional
    public void updateFileContent(Long fileId, String newContent) {
        RoomFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        file.setContent(newContent);
        fileRepository.save(file);
    }

    @Transactional
    public FileDto renameFile(Long fileId, String newName) {
        RoomFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        file.setFileName(newName);
        
        // Update language if extension changed
        if (newName.endsWith(".html")) file.setLanguage("html");
        else if (newName.endsWith(".css")) file.setLanguage("css");
        else if (newName.endsWith(".java")) file.setLanguage("java");
        else if (newName.endsWith(".json")) file.setLanguage("json");
        else if (newName.endsWith(".js") || newName.endsWith(".jsx")) file.setLanguage("javascript");

        RoomFile saved = fileRepository.save(file);
        return mapToDto(saved);
    }

    private FileDto mapToDto(RoomFile file) {
        return FileDto.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .language(file.getLanguage())
                .content(file.getContent())
                .roomId(file.getRoom().getId())
                .isDirectory(file.getIsDirectory())
                .parentId(file.getParentId())
                .build();
    }

    private String generateBoilerplate(String fileName) {
        if (fileName == null) return "";
        String lowerName = fileName.toLowerCase();
        
        if (lowerName.endsWith(".html")) {
            return "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\">\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n  <title>" + fileName + "</title>\n</head>\n<body>\n  \n</body>\n</html>";
        } else if (lowerName.endsWith(".css")) {
            return "/* Styles for " + fileName + " */\n\n* {\n  margin: 0;\n  padding: 0;\n  box-sizing: border-box;\n}\n\nbody {\n  font-family: sans-serif;\n}\n";
        } else if (lowerName.endsWith(".js") || lowerName.endsWith(".jsx")) {
            return "/**\n * " + fileName + "\n */\n\nconsole.log('" + fileName + " loaded!');\n";
        } else if (lowerName.endsWith(".java")) {
            String className = fileName.substring(0, fileName.lastIndexOf('.'));
            // Ensure first letter is capitalized for Java convention
            if (className.length() > 0) {
                className = className.substring(0, 1).toUpperCase() + className.substring(1);
            } else {
                className = "Main";
            }
            return "public class " + className + " {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World from " + className + "!\");\n    }\n}\n";
        } else if (lowerName.endsWith(".json")) {
            return "{\n  \n}\n";
        }
        
        return "// New file: " + fileName + "\n";
    }
}
