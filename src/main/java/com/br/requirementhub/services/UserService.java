package com.br.requirementhub.services;

import static com.br.requirementhub.enums.Role.ADMIN;

import com.br.requirementhub.dtos.user.UserRequestDTO;
import com.br.requirementhub.dtos.user.UserResponseDTO;
import com.br.requirementhub.entity.Requirement;
import com.br.requirementhub.entity.User;
import com.br.requirementhub.enums.Role;
import com.br.requirementhub.exceptions.UserNotFoundException;
import com.br.requirementhub.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository repository;

    private final UserRepository userRepository;

    public List<UserResponseDTO> getAllUsers() {
        return repository.findAll().stream()
                .filter(user -> !user.getRole().equals(ADMIN))
                .map(this::convertToResponseDTO)
                .sorted(Comparator.comparing(UserResponseDTO::getRole))
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> findByRole(Role role) {
        return repository.findByRole(role).stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getName(), user.getRole(), user.getImage()))
                .collect(Collectors.toList());
    }

    public Role findUserRoleById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId))
                .getRole();
    }

    public UserResponseDTO getById(Long id) {
        Optional<User> user = Optional.ofNullable(repository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found")
        ));

        return user.map(this::convertToResponseDTO).orElse(null);
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getRole(), user.getImage());
    }

    private User convertToEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setRole(dto.getRole());
        return user;
    }

    @Transactional
    public UserResponseDTO updateUserImage(Long id, String imageBase64) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setImage(imageBase64);
        repository.save(user);

        return convertToResponseDTO(user);
    }

    public List<Long> getUserNotifications(Long userId) {
        User user = userRepository.getReferenceById(userId);
        return user.getNotifications().stream().map(Requirement::getId).collect(Collectors.toList());
    }

    public void deleteNotification(Long userId, Long requirementId) {
        repository.deleteNotification(userId, requirementId);
    }
}
