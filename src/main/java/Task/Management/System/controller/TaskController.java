package Task.Management.System.controller;

import Task.Management.System.dto.request.task.CreateTaskRequest;
import Task.Management.System.dto.request.task.UpdateTaskRequest;
import Task.Management.System.dto.request.task.UpdateTaskStatusRequest;
import Task.Management.System.dto.response.common.ApiResponse;
import Task.Management.System.dto.response.task.TaskResponse;
import Task.Management.System.entity.User;
import Task.Management.System.enums.TaskStatus;
import Task.Management.System.service.AuthService;
import Task.Management.System.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTasks() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(taskService.getAllTasks(user)));
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getMyTasks() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(taskService.getMyTasks(user)));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> filterTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long projectId) {
        User user = authService.getCurrentUser();
        TaskStatus taskStatus = status != null ? TaskStatus.valueOf(status.toUpperCase()) : null;
        List<TaskResponse> all = taskService.getAllTasks(user);
        List<TaskResponse> filtered = all.stream()
                .filter(t -> taskStatus == null || t.getStatus() == taskStatus)
                .filter(t -> priority == null || t.getPriority().name().equalsIgnoreCase(priority))
                .filter(t -> projectId == null || t.getProject().getId().equals(projectId))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(filtered));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody CreateTaskRequest request) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Task created", taskService.createTask(request, user)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskById(id, user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(@PathVariable Long id,
                                                                  @Valid @RequestBody UpdateTaskRequest request) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTask(id, request, user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        taskService.deleteTask(id, user);
        return ResponseEntity.ok(ApiResponse.success("Task deleted", null));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<TaskResponse>> assignTask(@PathVariable Long id,
                                                                  @RequestParam Long userId) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(taskService.assignTask(id, userId, user)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(@PathVariable Long id,
                                                                    @Valid @RequestBody UpdateTaskStatusRequest request) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTaskStatus(id, request.getStatus(), user)));
    }
}
