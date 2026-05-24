package com.example.stockmvp.store.application;

import com.example.stockmvp.store.domain.Store;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class StoreDtos {

    private StoreDtos() {
    }

    public record CreateStoreRequest(
            @NotBlank @Size(max = 50) String code,
            @NotBlank @Size(max = 150) String name
    ) {
    }

    public record UpdateStoreRequest(
            @NotBlank @Size(max = 50) String code,
            @NotBlank @Size(max = 150) String name,
            boolean active
    ) {
    }

    public record StoreResponse(
            Long id,
            String code,
            String name,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static StoreResponse from(Store store) {
            return new StoreResponse(
                    store.getId(),
                    store.getCode(),
                    store.getName(),
                    store.isActive(),
                    store.getCreatedAt(),
                    store.getUpdatedAt()
            );
        }
    }
}
