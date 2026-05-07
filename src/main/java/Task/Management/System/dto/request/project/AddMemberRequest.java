package Task.Management.System.dto.request.project;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMemberRequest {
    @NotNull
    private Long userId;
}
