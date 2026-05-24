package com.example.stockmvp.stock.application;

import com.example.stockmvp.stock.domain.Stock;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public final class StockDtos {

    private StockDtos() {
    }

    public record CreateStockRequest(
            @NotNull Long productId,
            @NotNull Long storeId,
            @NotNull @Min(0) Integer currentQuantity,
            @NotNull @Min(0) Integer minimumQuantity
    ) {
    }

    public record UpdateStockRequest(
            @NotNull @Min(0) Integer currentQuantity,
            @NotNull @Min(0) Integer minimumQuantity
    ) {
    }

    public record StockResponse(
            Long stockId,
            Long productId,
            String productName,
            Long storeId,
            String storeName,
            Integer currentQuantity,
            Integer minimumQuantity,
            boolean critical
    ) {
        public static StockResponse from(Stock stock) {
            return new StockResponse(
                    stock.getId(),
                    stock.getProduct().getId(),
                    stock.getProduct().getName(),
                    stock.getStore().getId(),
                    stock.getStore().getName(),
                    stock.getCurrentQuantity(),
                    stock.getMinimumQuantity(),
                    stock.isCritical()
            );
        }
    }
}
