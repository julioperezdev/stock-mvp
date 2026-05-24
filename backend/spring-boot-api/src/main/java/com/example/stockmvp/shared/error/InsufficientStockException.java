package com.example.stockmvp.shared.error;

import java.util.Map;

public class InsufficientStockException extends RuntimeException {

    private final Map<String, Object> details;

    public InsufficientStockException(Long productId, Integer requestedQuantity, Integer availableQuantity) {
        super("No hay stock suficiente para completar la venta");
        this.details = Map.of(
                "productId", productId,
                "requestedQuantity", requestedQuantity,
                "availableQuantity", availableQuantity
        );
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
