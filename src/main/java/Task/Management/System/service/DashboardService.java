package Task.Management.System.service;

import Task.Management.System.dto.response.dashboard.DashboardStatsResponse;
import Task.Management.System.dto.response.dashboard.RecentTaskResponse;
import Task.Management.System.dto.response.task.TaskSummaryResponse;
import Task.Management.System.entity.Project;
import Task.Management.System.entity.Task;
import Task.Management.System.entity.User;
import Task.Management.System.enums.RoleType;
import Task.Management.System.enums.TaskPriority;
import Task.Management.System.enums.TaskStatus;
import Task.Management.System.enums.ProjectStatus;
import Task.Management.System.repository.ProjectMemberRepository;
import Task.Management.System.repository.ProjectRepository;
import Task.Management.System.repository.TaskRepository;
import Task.Management.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    public DashboardStatsResponse getDashboardStats(User user) {
        DashboardStatsResponse stats = new DashboardStatsResponse();

        if (user.getRole() == RoleType.ADMIN) {
            List<Project> allProjects = projectRepository.findAll();
            stats.setTotalProjects(allProjects.size());
            stats.setActiveProjects(allProjects.stream().filter(p -> p.getStatus() == ProjectStatus.ACTIVE).count());
            stats.setCompletedProjects(allProjects.stream().filter(p -> p.getStatus() == ProjectStatus.COMPLETED).count());

            List<Task> allTasks = taskRepository.findAll();
            stats.setTotalTasks(allTasks.size());
            stats.setTodoTasks(allTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count());
            stats.setInProgressTasks(allTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count());
            stats.setCompletedTasks(allTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count());
            stats.setHighPriorityTasks(allTasks.stream().filter(t -> t.getPriority() == TaskPriority.HIGH).count());
            stats.setTotalUsers(userRepository.findByIsActiveTrue().size());
        } else {
            List<Long> projectIds = memberRepository.findProjectIdsByUserId(user.getId());
            List<Project> myProjects = projectIds.isEmpty() ? List.of() : projectRepository.findAllById(projectIds);
            stats.setTotalProjects(myProjects.size());
            stats.setActiveProjects(myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.ACTIVE).count());
            stats.setCompletedProjects(myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.COMPLETED).count());

            List<Task> myTasks = projectIds.isEmpty() ? List.of() : taskRepository.findByProjectIdIn(projectIds);
            stats.setTotalTasks(myTasks.size());
            stats.setTodoTasks(myTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count());
            stats.setInProgressTasks(myTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count());
            stats.setCompletedTasks(myTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count());
            stats.setHighPriorityTasks(myTasks.stream().filter(t -> t.getPriority() == TaskPriority.HIGH).count());
        }

        return stats;
    }

    public List<RecentTaskResponse> getRecentTasks(User user) {
        List<Task> tasks;
        if (user.getRole() == RoleType.ADMIN) {
            tasks = taskRepository.findAll().stream()
                    .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                    .limit(10)
                    .collect(Collectors.toList());
        } else {
            List<Long> projectIds = memberRepository.findProjectIdsByUserId(user.getId());
            tasks = projectIds.isEmpty() ? List.of() :
                    taskRepository.findTop10ByProjectIdInOrderByCreatedAtDesc(projectIds);
        }
        return tasks.stream().map(RecentTaskResponse::from).collect(Collectors.toList());
    }

    public List<TaskSummaryResponse> getUpcomingDeadlines(User user) {
        LocalDate today = LocalDate.now();
        LocalDate inSevenDays = today.plusDays(7);

        List<Task> tasks;
        if (user.getRole() == RoleType.ADMIN) {
            tasks = taskRepository.findAll().stream()
                    .filter(t -> t.getDueDate() != null &&
                            !t.getDueDate().isBefore(today) &&
                            !t.getDueDate().isAfter(inSevenDays) &&
                            t.getStatus() != TaskStatus.DONE)
                    .collect(Collectors.toList());
        } else {
            List<Long> projectIds = memberRepository.findProjectIdsByUserId(user.getId());
            tasks = projectIds.isEmpty() ? List.of() :
                    taskRepository.findByProjectIdInAndDueDateBetween(projectIds, today, inSevenDays).stream()
                            .filter(t -> t.getStatus() != TaskStatus.DONE)
                            .collect(Collectors.toList());
        }

        return tasks.stream().map(TaskSummaryResponse::from).collect(Collectors.toList());
    }
}
