package Task.Management.System.dto.request.task;

import Task.Management.System.enums.TaskPriority;
import Task.Management.System.enums.TaskStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {
    @Size(min = 3, max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long assignedToId;
}
