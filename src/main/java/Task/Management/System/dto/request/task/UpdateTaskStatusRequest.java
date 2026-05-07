package Task.Management.System.dto.request.task;

import Task.Management.System.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {
    @NotNull
    private TaskStatus status;
}
