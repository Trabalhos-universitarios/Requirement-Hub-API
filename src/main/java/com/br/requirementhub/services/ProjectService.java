package com.br.requirementhub.services;

import com.br.requirementhub.dtos.ProjectRequestDTO;
import com.br.requirementhub.dtos.ProjectResponseDTO;
import com.br.requirementhub.entity.Project;
import com.br.requirementhub.entity.Team;
import com.br.requirementhub.entity.User;
import com.br.requirementhub.repository.ProjectRepository;
import com.br.requirementhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponseDTO create(ProjectRequestDTO requestDTO) throws IOException {
        Project project = convertToEntity(requestDTO);
        Project savedProject = projectRepository.save(project);

        List<Team> teams = savedProject.getTeams();
        for (Team team : teams) {
            team.setProject(savedProject);
        }
        projectRepository.save(savedProject);

        return convertToDTO(savedProject);
    }

    public List<ProjectResponseDTO> list() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProjectResponseDTO findById(Long id) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isPresent()) {
            return convertToDTO(project.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with id: " + id);
        }
    }

    private ProjectResponseDTO convertToDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setManager(project.getManager());
        dto.setStatus(project.getStatus());
        dto.setDescription(project.getDescription());
        dto.setVersion(project.getVersion());
        dto.setCreationDate(project.getCreationDate());
        dto.setLastUpdate(project.getLastUpdate());

        List<String> requirementAnalysts = project.getTeams().stream()
                .filter(team -> "REQUIREMENT_ANALYST".equals(team.getUser().getRole().name()))
                .map(team -> userRepository.findById(team.getUser().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")).getName())
                .collect(Collectors.toList());
        dto.setRequirementAnalysts(requirementAnalysts);

        List<String> businessAnalysts = project.getTeams().stream()
                .filter(team -> "BUSINESS_ANALYST".equals(team.getUser().getRole().name()))
                .map(team -> userRepository.findById(team.getUser().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")).getName())
                .collect(Collectors.toList());
        dto.setBusinessAnalysts(businessAnalysts);

        List<String> commonUsers = project.getTeams().stream()
                .filter(team -> "COMMON_USER".equals(team.getUser().getRole().name()))
                .map(team -> userRepository.findById(team.getUser().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")).getName())
                .collect(Collectors.toList());
        dto.setCommonUsers(commonUsers);

        return dto;
    }

    private Project convertToEntity(ProjectRequestDTO dto) throws IOException {
        Project project = new Project();
        project.setName(dto.getName());
        project.setManager(dto.getManager());
        project.setStatus(dto.getStatus());
        project.setDescription(dto.getDescription());
        project.setVersion(dto.getVersion());
        project.setCreationDate(new Date());

        List<Team> teams = dto.getRequirementAnalysts().stream()
                .map(userId -> createTeam(userId, project))
                .collect(Collectors.toList());
        teams.addAll(dto.getBusinessAnalysts().stream()
                .map(userId -> createTeam(userId, project))
                .collect(Collectors.toList()));
        teams.addAll(dto.getCommonUsers().stream()
                .map(userId -> createTeam(userId, project))
                .collect(Collectors.toList()));

        project.setTeams(teams);

        return project;
    }

    private Team createTeam(Long userId, Project project) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));
        Team team = new Team();
        team.setUser(user);
        team.setProject(project);
        return team;
    }
}