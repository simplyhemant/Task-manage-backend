package Task.Management.System.dto.response.project;

import Task.Management.System.dto.response.user.UserSummaryResponse;
import Task.Management.System.entity.Project;
import Task.Management.System.enums.ProjectStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private UserSummaryResponse owner;
    private long taskCount;
    private long memberCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProjectResponse from(Project p, long taskCount, long memberCount) {
        ProjectResponse r = new ProjectResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setStatus(p.getStatus());
        r.setStartDate(p.getStartDate());
        r.setEndDate(p.getEndDate());
        r.setOwner(UserSummaryResponse.from(p.getOwner()));
        r.setTaskCount(taskCount);
        r.setMemberCount(memberCount);
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        return r;
    }
}
