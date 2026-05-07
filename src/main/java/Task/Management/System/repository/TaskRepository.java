package Task.Management.System.repository;

import Task.Management.System.entity.Task;
import Task.Management.System.enums.TaskPriority;
import Task.Management.System.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByAssignedToId(Long userId);
    List<Task> findByCreatedById(Long userId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByPriority(TaskPriority priority);
    List<Task> findByAssignedToIdAndStatus(Long userId, TaskStatus status);
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
    List<Task> findTop10ByAssignedToIdOrderByCreatedAtDesc(Long userId);
    List<Task> findByAssignedToIdAndDueDateBetween(Long userId, LocalDate start, LocalDate end);
    long countByProjectIdAndStatus(Long projectId, TaskStatus status);
    List<Task> findByProjectIdIn(List<Long> projectIds);
    List<Task> findByProjectIdInAndStatus(List<Long> projectIds, TaskStatus status);
    List<Task> findByProjectIdInAndPriority(List<Long> projectIds, TaskPriority priority);
    List<Task> findTop10ByProjectIdInOrderByCreatedAtDesc(List<Long> projectIds);
    List<Task> findByProjectIdInAndDueDateBetween(List<Long> projectIds, LocalDate start, LocalDate end);
    long countByStatus(TaskStatus status);
    long countByPriority(TaskPriority priority);
}
