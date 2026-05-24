package com.example.stockmvp.auth.application;

import com.example.stockmvp.auth.domain.AppUser;
import com.example.stockmvp.auth.infrastructure.AppUserRepository;
import com.example.stockmvp.shared.error.ResourceNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        AppUser user = appUserRepository.findByEmail(request.email())
                .filter(AppUser::isActive)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return new AuthDtos.LoginResponse(
                jwtService.generateToken(user),
                "Bearer",
                jwtService.getExpirationSeconds(),
                AuthDtos.UserResponse.from(user)
        );
    }

    @Transactional(readOnly = true)
    public AuthDtos.UserResponse getUser(String email) {
        return appUserRepository.findByEmail(email)
                .map(AuthDtos.UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
