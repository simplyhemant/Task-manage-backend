import os

base_path = "src/main/java/Task/Management/System"
dirs = ["repository", "dto/request", "dto/response", "controller", "service", "security", "config", "exception", "util"]

for d in dirs:
    os.makedirs(f"{base_path}/{d}", exist_ok=True)

# Repositories
repos = ["User", "Project", "Task", "ProjectMember", "RefreshToken"]
for r in repos:
    with open(f"{base_path}/repository/{r}Repository.java", "w") as f:
        f.write(f"""package Task.Management.System.repository;

import Task.Management.System.entity.{r};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface {r}Repository extends JpaRepository<{r}, Long> {{
}}
""")

print("Scaffolding complete")
