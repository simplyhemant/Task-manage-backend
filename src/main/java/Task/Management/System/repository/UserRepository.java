package Task.Management.System.repository;

import Task.Management.System.entity.User;
import Task.Management.System.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByIsActiveTrue();
    boolean existsByEmail(String email);
    List<User> findByRole(RoleType role);
    long countByRole(RoleType role);
}
