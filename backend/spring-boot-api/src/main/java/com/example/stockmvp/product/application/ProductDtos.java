package com.example.stockmvp.product.application;

import com.example.stockmvp.product.domain.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class ProductDtos {

    private ProductDtos() {
    }

    public record CreateProductRequest(
            @NotBlank @Size(max = 50) String sku,
            @NotBlank @Size(max = 150) String name,
            @Size(max = 255) String description
    ) {
    }

    public record UpdateProductRequest(
            @NotBlank @Size(max = 50) String sku,
            @NotBlank @Size(max = 150) String name,
            @Size(max = 255) String description,
            boolean active
    ) {
    }

    public record ProductResponse(
            Long id,
            String sku,
            String name,
            String description,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static ProductResponse from(Product product) {
            return new ProductResponse(
                    product.getId(),
                    product.getSku(),
                    product.getName(),
                    product.getDescription(),
                    product.isActive(),
                    product.getCreatedAt(),
                    product.getUpdatedAt()
            );
        }
    }
}
