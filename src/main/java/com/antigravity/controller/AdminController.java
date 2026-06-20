package com.antigravity.controller;

import com.antigravity.dto.AdminStatsResponse;
import com.antigravity.dto.ProjectDto;
import com.antigravity.dto.RoomDto;
import com.antigravity.dto.UserDto;
import com.antigravity.repository.ProjectRepository;
import com.antigravity.repository.RoomRepository;
import com.antigravity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final RoomRepository roomRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminStatsResponse> getStats() {
        AdminStatsResponse stats = AdminStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalProjects(projectRepository.count())
                .totalRooms(roomRepository.count())
                .build();
                
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(u -> UserDto.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/projects")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projects = projectRepository.findAll().stream()
                .map(p -> ProjectDto.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .ownerId(p.getOwner().getId())
                        .ownerUsername(p.getOwner().getUsername())
                        .createdAt(p.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        List<RoomDto> rooms = roomRepository.findAll().stream()
                .map(r -> RoomDto.builder()
                        .id(r.getId())
                        .roomCode(r.getRoomCode())
                        .name(r.getName())
                        .projectId(r.getProject() != null ? r.getProject().getId() : null)
                        .projectName(r.getProject() != null ? r.getProject().getName() : null)
                        .hostId(r.getHost().getId())
                        .hostUsername(r.getHost().getUsername())
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(rooms);
    }
}
