package Task.Management.System.controller;

import Task.Management.System.dto.request.user.ChangePasswordRequest;
import Task.Management.System.dto.request.user.UpdateProfileRequest;
import Task.Management.System.dto.request.user.UpdateUserRequest;
import Task.Management.System.dto.response.common.ApiResponse;
import Task.Management.System.dto.response.user.UserResponse;
import Task.Management.System.entity.User;
import Task.Management.System.service.AuthService;
import Task.Management.System.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id,
                                                                 @Valid @RequestBody UpdateUserRequest request) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, request, currentUser)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        userService.deactivateUser(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("User deactivated", null));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@RequestBody UpdateProfileRequest request) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(userService.updateProfile(request, currentUser)));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User currentUser = authService.getCurrentUser();
        userService.changePassword(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}
