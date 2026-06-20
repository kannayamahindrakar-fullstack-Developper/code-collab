package com.antigravity.service;

import com.antigravity.dto.RoomDto;
import com.antigravity.dto.RoomRequest;
import com.antigravity.entity.Project;
import com.antigravity.entity.Room;
import com.antigravity.entity.RoomParticipant;
import com.antigravity.entity.User;
import com.antigravity.repository.ProjectRepository;
import com.antigravity.repository.RoomParticipantRepository;
import com.antigravity.repository.RoomRepository;
import com.antigravity.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final com.antigravity.repository.RoomFileRepository roomFileRepository;

    public RoomService(RoomRepository roomRepository, 
                       ProjectRepository projectRepository, 
                       UserRepository userRepository, 
                       RoomParticipantRepository roomParticipantRepository,
                       com.antigravity.repository.RoomFileRepository roomFileRepository) {
        this.roomRepository = roomRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.roomParticipantRepository = roomParticipantRepository;
        this.roomFileRepository = roomFileRepository;
    }

    public RoomDto createRoom(RoomRequest request, String username) {
        User host = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = null;
        if (request.getProjectId() != null) {
            project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            
            if (!project.getOwner().getId().equals(host.getId())) {
                throw new RuntimeException("Unauthorized: You must own the project to create a room in it");
            }
        }

        // Generate 6 character alphanumeric code
        String roomCode = generateRoomCode();

        Room room = Room.builder()
                .name(request.getName())
                .roomCode(roomCode)
                .project(project)
                .host(host)
                .build();

        Room savedRoom = roomRepository.save(room);

        // Auto-join the host as a participant
        joinRoom(savedRoom.getId(), username);

        return mapToDto(savedRoom);
    }

    public RoomDto joinRoomByCode(String roomCode, String username) {
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        return joinRoom(room.getId(), username);
    }

    public RoomDto joinRoom(Long roomId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Check if already a participant
        Optional<RoomParticipant> existingParticipant = roomParticipantRepository.findByRoomIdAndUserId(room.getId(), user.getId());
        
        if (existingParticipant.isEmpty()) {
            RoomParticipant participant = RoomParticipant.builder()
                    .room(room)
                    .user(user)
                    .permission(room.getHost().getId().equals(user.getId()) ? "ADMIN" : "READ_WRITE")
                    .build();
            roomParticipantRepository.save(participant);
        }

        return mapToDto(room);
    }

    public List<RoomDto> getUserRooms(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return roomParticipantRepository.findByUserId(user.getId())
                .stream()
                .map(RoomParticipant::getRoom)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional
    public String deleteRoom(Long roomId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only ADMIN can delete a room
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Unauthorized: Only Admin can delete rooms");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        String roomCode = room.getRoomCode();

        // Delete dependencies to avoid Foreign Key violations
        roomFileRepository.deleteAll(roomFileRepository.findByRoomId(roomId));
        roomParticipantRepository.deleteAll(roomParticipantRepository.findByRoomId(roomId));
        
        // Delete the room
        roomRepository.delete(room);
        
        return roomCode;
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private RoomDto mapToDto(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .roomCode(room.getRoomCode())
                .name(room.getName())
                .projectId(room.getProject() != null ? room.getProject().getId() : null)
                .projectName(room.getProject() != null ? room.getProject().getName() : null)
                .hostId(room.getHost().getId())
                .hostUsername(room.getHost().getUsername())
                .createdAt(room.getCreatedAt())
                .build();
    }
}
