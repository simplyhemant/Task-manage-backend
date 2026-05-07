package Task.Management.System.dto.request.user;

import Task.Management.System.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Size(min = 3, max = 50)
    private String name;

    @Email
    private String email;

    private RoleType role;
    private Boolean isActive;
}
