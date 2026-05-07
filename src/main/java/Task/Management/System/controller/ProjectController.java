package Task.Management.System.controller;

import Task.Management.System.dto.request.project.AddMemberRequest;
import Task.Management.System.dto.request.project.CreateProjectRequest;
import Task.Management.System.dto.request.project.UpdateProjectRequest;
import Task.Management.System.dto.response.common.ApiResponse;
import Task.Management.System.dto.response.project.ProjectResponse;
import Task.Management.System.dto.response.task.TaskResponse;
import Task.Management.System.dto.response.user.UserSummaryResponse;
import Task.Management.System.entity.User;
import Task.Management.System.service.AuthService;
import Task.Management.System.service.ProjectService;
import Task.Management.System.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(projectService.getAllProjects(user)));
    }

    @GetMapping("/my-projects")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getMyProjects() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(projectService.getMyProjects(user)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(@Valid @RequestBody CreateProjectRequest request) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Project created", projectService.createProject(request, user)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(projectService.getProjectById(id, user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(@PathVariable Long id,
                                                                        @Valid @RequestBody UpdateProjectRequest request) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(projectService.updateProject(id, request, user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        projectService.deleteProject(id, user);
        return ResponseEntity.ok(ApiResponse.success("Project deleted", null));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getMembers(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(projectService.getProjectMembers(id, user)));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> addMember(@PathVariable Long id,
                                                                        @Valid @RequestBody AddMemberRequest request) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Member added", projectService.addMember(id, request, user)));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        User user = authService.getCurrentUser();
        projectService.removeMember(id, userId, user);
        return ResponseEntity.ok(ApiResponse.success("Member removed", null));
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getProjectTasks(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(taskService.getTasksByProject(id, user)));
    }
}
