package Task.Management.System.service;

import Task.Management.System.dto.request.user.ChangePasswordRequest;
import Task.Management.System.dto.request.user.UpdateProfileRequest;
import Task.Management.System.dto.request.user.UpdateUserRequest;
import Task.Management.System.dto.response.user.UserResponse;
import Task.Management.System.entity.User;
import Task.Management.System.enums.RoleType;
import Task.Management.System.exception.BadRequestException;
import Task.Management.System.exception.ForbiddenException;
import Task.Management.System.exception.ResourceNotFoundException;
import Task.Management.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        return userRepository.findByIsActiveTrue().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return UserResponse.from(findUserById(id));
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request, User currentUser) {
        if (currentUser.getRole() != RoleType.ADMIN) {
            throw new ForbiddenException("Only admins can update other users");
        }
        User user = findUserById(id);
        if (request.getName() != null) user.setName(request.getName());
        if (request.getEmail() != null) {
            if (userRepository.existsByEmail(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
                throw new BadRequestException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getIsActive() != null) user.setIsActive(request.getIsActive());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request, User currentUser) {
        if (request.getName() != null) currentUser.setName(request.getName());
        if (request.getProfileImageUrl() != null) currentUser.setProfileImageUrl(request.getProfileImageUrl());
        return UserResponse.from(userRepository.save(currentUser));
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request, User currentUser) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }

    @Transactional
    public void deactivateUser(Long id, User currentUser) {
        if (currentUser.getRole() != RoleType.ADMIN) {
            throw new ForbiddenException("Only admins can deactivate users");
        }
        User user = findUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
