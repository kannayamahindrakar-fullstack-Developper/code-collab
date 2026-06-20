package com.antigravity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminStatsResponse {
    private long totalUsers;
    private long totalProjects;
    private long totalRooms;
}
