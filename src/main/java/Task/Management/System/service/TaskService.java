package Task.Management.System.service;

import Task.Management.System.dto.request.task.CreateTaskRequest;
import Task.Management.System.dto.request.task.UpdateTaskRequest;
import Task.Management.System.dto.response.task.TaskResponse;
import Task.Management.System.entity.Task;
import Task.Management.System.entity.User;
import Task.Management.System.enums.RoleType;
import Task.Management.System.enums.TaskStatus;
import Task.Management.System.exception.BadRequestException;
import Task.Management.System.exception.ForbiddenException;
import Task.Management.System.exception.ResourceNotFoundException;
import Task.Management.System.repository.ProjectMemberRepository;
import Task.Management.System.repository.ProjectRepository;
import Task.Management.System.repository.TaskRepository;
import Task.Management.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    public List<TaskResponse> getAllTasks(User user) {
        List<Task> tasks;
        if (user.getRole() == RoleType.ADMIN) {
            tasks = taskRepository.findAll();
        } else {
            List<Long> projectIds = memberRepository.findProjectIdsByUserId(user.getId());
            tasks = projectIds.isEmpty() ? List.of() : taskRepository.findByProjectIdIn(projectIds);
        }
        return tasks.stream().map(TaskResponse::from).collect(Collectors.toList());
    }

    public List<TaskResponse> getMyTasks(User user) {
        return taskRepository.findByAssignedToId(user.getId()).stream()
                .map(TaskResponse::from).collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByProject(Long projectId, User user) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (user.getRole() != RoleType.ADMIN &&
                !memberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new ForbiddenException("You don't have access to this project");
        }
        return taskRepository.findByProjectId(projectId).stream()
                .map(TaskResponse::from).collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id, User user) {
        Task task = findById(id);
        checkReadAccess(task, user);
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, User creator) {
        var project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (creator.getRole() != RoleType.ADMIN &&
                !memberRepository.existsByProjectIdAndUserId(project.getId(), creator.getId())) {
            throw new ForbiddenException("You must be a project member to create tasks");
        }
        User assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            if (!memberRepository.existsByProjectIdAndUserId(project.getId(), request.getAssignedToId())) {
                throw new BadRequestException("Assignee must be a member of the project");
            }
        }
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .project(project)
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .assignedTo(assignedTo)
                .createdBy(creator)
                .build();
        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest request, User user) {
        Task task = findById(id);
        checkWriteAccess(task, user);
        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            if (!memberRepository.existsByProjectIdAndUserId(task.getProject().getId(), request.getAssignedToId())) {
                throw new BadRequestException("Assignee must be a project member");
            }
            task.setAssignedTo(assignedTo);
        }
        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id, User user) {
        Task task = findById(id);
        if (user.getRole() != RoleType.ADMIN &&
                !task.getProject().getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("Only project owner or admin can delete tasks");
        }
        taskRepository.delete(task);
    }

    @Transactional
    public TaskResponse assignTask(Long taskId, Long userId, User currentUser) {
        Task task = findById(taskId);
        if (currentUser.getRole() != RoleType.ADMIN &&
                !task.getProject().getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only project owner or admin can assign tasks");
        }
        User assignedTo = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!memberRepository.existsByProjectIdAndUserId(task.getProject().getId(), userId)) {
            throw new BadRequestException("User must be a project member");
        }
        task.setAssignedTo(assignedTo);
        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, TaskStatus status, User user) {
        Task task = findById(taskId);
        boolean isAdmin = user.getRole() == RoleType.ADMIN;
        boolean isOwner = task.getProject().getOwner().getId().equals(user.getId());
        boolean isAssignee = task.getAssignedTo() != null && task.getAssignedTo().getId().equals(user.getId());
        if (!isAdmin && !isOwner && !isAssignee) {
            throw new ForbiddenException("You don't have permission to update this task status");
        }
        task.setStatus(status);
        return TaskResponse.from(taskRepository.save(task));
    }

    private Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }

    private void checkReadAccess(Task task, User user) {
        if (user.getRole() == RoleType.ADMIN) return;
        if (!memberRepository.existsByProjectIdAndUserId(task.getProject().getId(), user.getId())) {
            throw new ForbiddenException("You don't have access to this task");
        }
    }

    private void checkWriteAccess(Task task, User user) {
        if (user.getRole() == RoleType.ADMIN) return;
        boolean isOwner = task.getProject().getOwner().getId().equals(user.getId());
        boolean isAssignee = task.getAssignedTo() != null && task.getAssignedTo().getId().equals(user.getId());
        if (!isOwner && !isAssignee) {
            throw new ForbiddenException("You don't have permission to update this task");
        }
    }
}
