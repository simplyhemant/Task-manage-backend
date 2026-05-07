package Task.Management.System.dto.request.project;

import Task.Management.System.enums.ProjectStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProjectRequest {
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private ProjectStatus status;
    private LocalDate endDate;
}
