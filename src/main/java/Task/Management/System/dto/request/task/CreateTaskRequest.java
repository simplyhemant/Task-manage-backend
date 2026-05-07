package Task.Management.System.dto.request.task;

import Task.Management.System.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {
    @NotBlank
    @Size(min = 3, max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull
    private Long projectId;

    private TaskPriority priority = TaskPriority.MEDIUM;
    private LocalDate dueDate;
    private Long assignedToId;
}
