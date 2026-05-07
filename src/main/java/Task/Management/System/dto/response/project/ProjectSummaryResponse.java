package Task.Management.System.dto.response.project;

import Task.Management.System.entity.Project;
import Task.Management.System.enums.ProjectStatus;
import lombok.Data;

@Data
public class ProjectSummaryResponse {
    private Long id;
    private String name;
    private ProjectStatus status;

    public static ProjectSummaryResponse from(Project p) {
        ProjectSummaryResponse r = new ProjectSummaryResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setStatus(p.getStatus());
        return r;
    }
}
