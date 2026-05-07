package Task.Management.System.service;

import Task.Management.System.dto.request.project.AddMemberRequest;
import Task.Management.System.dto.request.project.CreateProjectRequest;
import Task.Management.System.dto.request.project.UpdateProjectRequest;
import Task.Management.System.dto.response.project.ProjectResponse;
import Task.Management.System.dto.response.user.UserSummaryResponse;
import Task.Management.System.entity.Project;
import Task.Management.System.entity.ProjectMember;
import Task.Management.System.entity.User;
import Task.Management.System.enums.RoleType;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<ProjectResponse> getAllProjects(User user) {
        List<Project> projects;
        if (user.getRole() == RoleType.ADMIN) {
            projects = projectRepository.findAll();
        } else {
            projects = projectRepository.findProjectsAccessibleByUser(user.getId(), user.getId());
        }
        return projects.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ProjectResponse> getMyProjects(User user) {
        return projectRepository.findByOwnerId(user.getId()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public ProjectResponse getProjectById(Long id, User user) {
        Project project = findProjectById(id);
        checkReadAccess(project, user);
        return toResponse(project);
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, User owner) {
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .owner(owner)
                .build();
        project = projectRepository.save(project);

        // Auto-add owner as member
        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(owner)
                .build();
        memberRepository.save(member);

        return toResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request, User user) {
        Project project = findProjectById(id);
        checkWriteAccess(project, user);

        if (request.getName() != null) project.setName(request.getName());
        if (request.getDescription() != null) project.setDescription(request.getDescription());
        if (request.getStatus() != null) project.setStatus(request.getStatus());
        if (request.getEndDate() != null) project.setEndDate(request.getEndDate());

        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long id, User user) {
        Project project = findProjectById(id);
        checkWriteAccess(project, user);

        // Delete associated tasks and members to avoid foreign key constraints
        taskRepository.deleteAll(taskRepository.findByProjectId(id));
        memberRepository.deleteAll(memberRepository.findByProjectId(id));

        projectRepository.delete(project);
    }

    public List<UserSummaryResponse> getProjectMembers(Long projectId, User user) {
        Project project = findProjectById(projectId);
        checkReadAccess(project, user);
        return memberRepository.findByProjectId(projectId).stream()
                .map(pm -> UserSummaryResponse.from(pm.getUser()))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserSummaryResponse addMember(Long projectId, AddMemberRequest request, User user) {
        Project project = findProjectById(projectId);
        checkWriteAccess(project, user);

        User newMember = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (memberRepository.existsByProjectIdAndUserId(projectId, request.getUserId())) {
            throw new BadRequestException("User is already a member of this project");
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(newMember)
                .build();
        memberRepository.save(member);
        return UserSummaryResponse.from(newMember);
    }

    @Transactional
    public void removeMember(Long projectId, Long userId, User currentUser) {
        Project project = findProjectById(projectId);
        checkWriteAccess(project, currentUser);

        if (project.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Cannot remove the project owner");
        }

        if (!memberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new ResourceNotFoundException("User is not a member of this project");
        }

        memberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    public boolean isUserMember(Long projectId, Long userId) {
        return memberRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    public boolean isUserOwner(Long projectId, Long userId) {
        return projectRepository.findById(projectId)
                .map(p -> p.getOwner().getId().equals(userId))
                .orElse(false);
    }

    private Project findProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    private void checkReadAccess(Project project, User user) {
        if (user.getRole() == RoleType.ADMIN) return;
        if (!project.getOwner().getId().equals(user.getId()) &&
                !memberRepository.existsByProjectIdAndUserId(project.getId(), user.getId())) {
            throw new ForbiddenException("You don't have access to this project");
        }
    }

    private void checkWriteAccess(Project project, User user) {
        if (user.getRole() == RoleType.ADMIN) return;
        if (!project.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("Only the project owner or admin can modify this project");
        }
    }



    private ProjectResponse toResponse(Project p) {
        long taskCount = taskRepository.findByProjectId(p.getId()).size();
        long memberCount = memberRepository.countByProjectId(p.getId());
        return ProjectResponse.from(p, taskCount, memberCount);
    }
}
