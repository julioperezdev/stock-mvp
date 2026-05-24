package com.example.stockmvp.auth.application;

import com.example.stockmvp.auth.domain.AppUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    public record LoginResponse(
            String accessToken,
            String tokenType,
            long expiresInSeconds,
            UserResponse user
    ) {
    }

    public record UserResponse(
            Long id,
            String email,
            String fullName,
            String role
    ) {
        public static UserResponse from(AppUser user) {
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRole().name()
            );
        }
    }
}
