package Task.Management.System.service;

import Task.Management.System.dto.request.auth.LoginRequest;
import Task.Management.System.dto.request.auth.RefreshTokenRequest;
import Task.Management.System.dto.request.auth.RegisterRequest;
import Task.Management.System.dto.response.auth.AuthResponse;
import Task.Management.System.dto.response.user.UserResponse;
import Task.Management.System.entity.RefreshToken;
import Task.Management.System.entity.User;
import Task.Management.System.enums.RoleType;
import Task.Management.System.exception.BadRequestException;
import Task.Management.System.exception.ResourceNotFoundException;
import Task.Management.System.repository.RefreshTokenRepository;
import Task.Management.System.repository.UserRepository;
import Task.Management.System.security.JwtUtils;
import Task.Management.System.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Value("${app.jwt.refreshExpirationMs}")
    private long refreshExpirationMs;

    @Value("${app.jwt.expirationMs}")
    private long jwtExpirationMs;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleType.USER)
                .isActive(true)
                .build();
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        String accessToken = jwtUtils.generateJwtToken(auth);

        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Delete old refresh token if exists
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .build();
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshToken.getToken(), jwtExpirationMs / 1000, UserResponse.from(user));
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BadRequestException("Refresh token expired, please login again");
        }

        User user = refreshToken.getUser();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                UserDetailsImpl.build(user), null, UserDetailsImpl.build(user).getAuthorities());

        String newAccessToken = jwtUtils.generateJwtToken(auth);
        return new AuthResponse(newAccessToken, refreshToken.getToken(), jwtExpirationMs / 1000, UserResponse.from(user));
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        refreshTokenRepository.findByToken(refreshTokenStr)
                .ifPresent(refreshTokenRepository::delete);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
