package com.antigravity.controller;

import com.antigravity.dto.ProjectDto;
import com.antigravity.dto.ProjectRequest;
import com.antigravity.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ProjectDto created = projectService.createProject(request, userDetails.getUsername());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getUserProjects(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<ProjectDto> projects = projectService.getUserProjects(userDetails.getUsername());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ProjectDto project = projectService.getProjectById(id, userDetails.getUsername());
        return ResponseEntity.ok(project);
    }
}
