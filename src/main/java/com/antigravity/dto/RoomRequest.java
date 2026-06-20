package com.antigravity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoomRequest {
    @NotBlank(message = "Room name is required")
    private String name;
    
    private Long projectId; // Optional: A room doesn't strictly need a project
}
