package com.example.stockmvp.shared.error;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        String code,
        String message,
        Object details,
        Instant timestamp
) {
    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(code, message, null, Instant.now());
    }

    public static ApiErrorResponse of(String code, String message, Object details) {
        return new ApiErrorResponse(code, message, details, Instant.now());
    }

    public record FieldErrorDetail(String field, String message) {
    }

    public static ApiErrorResponse validation(List<FieldErrorDetail> details) {
        return of("VALIDATION_ERROR", "Invalid request data", details);
    }
}
