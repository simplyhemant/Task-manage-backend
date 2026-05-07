package Task.Management.System.controller;

import Task.Management.System.dto.response.common.ApiResponse;
import Task.Management.System.dto.response.dashboard.DashboardStatsResponse;
import Task.Management.System.dto.response.dashboard.RecentTaskResponse;
import Task.Management.System.dto.response.task.TaskSummaryResponse;
import Task.Management.System.entity.User;
import Task.Management.System.service.AuthService;
import Task.Management.System.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthService authService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboardStats(user)));
    }

    @GetMapping("/recent-tasks")
    public ResponseEntity<ApiResponse<List<RecentTaskResponse>>> getRecentTasks() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getRecentTasks(user)));
    }

    @GetMapping("/upcoming-deadlines")
    public ResponseEntity<ApiResponse<List<TaskSummaryResponse>>> getUpcomingDeadlines() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getUpcomingDeadlines(user)));
    }
}
