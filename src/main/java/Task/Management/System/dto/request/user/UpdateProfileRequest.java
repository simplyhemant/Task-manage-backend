package Task.Management.System.dto.request.user;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String profileImageUrl;
}
