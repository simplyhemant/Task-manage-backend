package Task.Management.System.dto.response.user;

import Task.Management.System.entity.User;
import Task.Management.System.enums.RoleType;
import lombok.Data;

@Data
public class UserSummaryResponse {
    private Long id;
    private String name;
    private String email;
    private RoleType role;

    public static UserSummaryResponse from(User user) {
        UserSummaryResponse r = new UserSummaryResponse();
        r.setId(user.getId());
        r.setName(user.getName());
        r.setEmail(user.getEmail());
        r.setRole(user.getRole());
        return r;
    }
}
