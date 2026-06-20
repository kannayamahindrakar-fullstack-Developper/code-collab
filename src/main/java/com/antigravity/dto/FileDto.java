package com.antigravity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private Long id;
    private String fileName;
    private String language;
    private String content;
    private Long roomId;
    private Boolean isDirectory;
    private Long parentId;
}
