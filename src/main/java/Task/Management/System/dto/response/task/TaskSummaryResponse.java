package Task.Management.System.dto.response.task;

import Task.Management.System.entity.Task;
import Task.Management.System.enums.TaskPriority;
import Task.Management.System.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskSummaryResponse {
    private Long id;
    private String title;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private String projectName;
    private Long projectId;

    public static TaskSummaryResponse from(Task t) {
        TaskSummaryResponse r = new TaskSummaryResponse();
        r.setId(t.getId());
        r.setTitle(t.getTitle());
        r.setStatus(t.getStatus());
        r.setPriority(t.getPriority());
        r.setDueDate(t.getDueDate());
        r.setProjectName(t.getProject().getName());
        r.setProjectId(t.getProject().getId());
        return r;
    }
}
