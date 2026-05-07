package Task.Management.System.dto.response.user;

import Task.Management.System.entity.User;
import Task.Management.System.enums.RoleType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private RoleType role;
    private Boolean isActive;
    private String profileImageUrl;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setName(user.getName());
        r.setEmail(user.getEmail());
        r.setRole(user.getRole());
        r.setIsActive(user.getIsActive());
        r.setProfileImageUrl(user.getProfileImageUrl());
        r.setCreatedAt(user.getCreatedAt());
        return r;
    }
}
