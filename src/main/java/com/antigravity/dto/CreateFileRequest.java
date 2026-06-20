package com.antigravity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFileRequest {
    @NotBlank
    private String fileName;
    
    private String language;
    
    private Boolean isDirectory = false;
    
    private Long parentId;
}
