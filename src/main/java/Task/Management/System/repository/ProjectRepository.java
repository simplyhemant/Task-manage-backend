package Task.Management.System.repository;

import Task.Management.System.entity.Project;
import Task.Management.System.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId(Long ownerId);
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findByOwnerIdAndStatus(Long ownerId, ProjectStatus status);

    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId OR p.id IN " +
           "(SELECT pm.project.id FROM ProjectMember pm WHERE pm.user.id = :userId)")
    List<Project> findProjectsAccessibleByUser(@Param("ownerId") Long ownerId,
                                               @Param("userId") Long userId);
}
