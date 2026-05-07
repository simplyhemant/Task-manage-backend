package Task.Management.System.config;

import Task.Management.System.entity.User;
import Task.Management.System.enums.RoleType;
import Task.Management.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@taskflow.com")) {
            User admin = User.builder()
                    .name("System Admin")
                    .email("admin@taskflow.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(RoleType.ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user created: admin@taskflow.com / admin123");
        }
    }
}
