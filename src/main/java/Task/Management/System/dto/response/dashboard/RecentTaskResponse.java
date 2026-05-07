package Task.Management.System.dto.response.dashboard;

import Task.Management.System.entity.Task;
import Task.Management.System.enums.TaskPriority;
import Task.Management.System.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecentTaskResponse {
    private Long id;
    private String title;
    private TaskStatus status;
    private TaskPriority priority;
    private String projectName;
    private LocalDateTime updatedAt;

    public static RecentTaskResponse from(Task t) {
        RecentTaskResponse r = new RecentTaskResponse();
        r.setId(t.getId());
        r.setTitle(t.getTitle());
        r.setStatus(t.getStatus());
        r.setPriority(t.getPriority());
        r.setProjectName(t.getProject().getName());
        r.setUpdatedAt(t.getUpdatedAt());
        return r;
    }
}
