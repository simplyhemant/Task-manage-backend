package Task.Management.System.dto.response.task;

import Task.Management.System.dto.response.project.ProjectSummaryResponse;
import Task.Management.System.dto.response.user.UserSummaryResponse;
import Task.Management.System.entity.Task;
import Task.Management.System.enums.TaskPriority;
import Task.Management.System.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private ProjectSummaryResponse project;
    private UserSummaryResponse assignedTo;
    private UserSummaryResponse createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse from(Task t) {
        TaskResponse r = new TaskResponse();
        r.setId(t.getId());
        r.setTitle(t.getTitle());
        r.setDescription(t.getDescription());
        r.setStatus(t.getStatus());
        r.setPriority(t.getPriority());
        r.setDueDate(t.getDueDate());
        r.setProject(ProjectSummaryResponse.from(t.getProject()));
        if (t.getAssignedTo() != null) r.setAssignedTo(UserSummaryResponse.from(t.getAssignedTo()));
        r.setCreatedBy(UserSummaryResponse.from(t.getCreatedBy()));
        r.setCreatedAt(t.getCreatedAt());
        r.setUpdatedAt(t.getUpdatedAt());
        return r;
    }
}
