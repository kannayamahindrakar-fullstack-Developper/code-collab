package com.antigravity.service;

import com.antigravity.dto.ProjectDto;
import com.antigravity.dto.ProjectRequest;
import com.antigravity.entity.Project;
import com.antigravity.entity.User;
import com.antigravity.repository.ProjectRepository;
import com.antigravity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectDto createProject(ProjectRequest request, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .build();

        Project savedProject = projectRepository.save(project);
        return mapToDto(savedProject);
    }

    public List<ProjectDto> getUserProjects(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return projectRepository.findByOwnerId(owner.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ProjectDto getProjectById(Long id, String username) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Simple authorization check
        if (!project.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access to this project");
        }
        
        return mapToDto(project);
    }

    private ProjectDto mapToDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .ownerId(project.getOwner().getId())
                .ownerUsername(project.getOwner().getUsername())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
