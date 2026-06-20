package com.antigravity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;
    private String roomCode;
    private String name;
    private Long projectId;
    private String projectName;
    private Long hostId;
    private String hostUsername;
    private LocalDateTime createdAt;
}
