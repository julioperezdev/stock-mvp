package com.example.stockmvp.sale.application;

import com.example.stockmvp.sale.domain.Sale;
import com.example.stockmvp.sale.domain.SaleItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public final class SaleDtos {

    private SaleDtos() {
    }

    public record CreateSaleRequest(
            @NotNull Long storeId,
            @Size(max = 100) String createdBy,
            @NotEmpty List<@Valid CreateSaleItemRequest> items
    ) {
    }

    public record CreateSaleItemRequest(
            @NotNull Long productId,
            @NotNull @Min(1) Integer quantity
    ) {
    }

    public record SaleResponse(
            Long saleId,
            Long storeId,
            String storeName,
            LocalDateTime createdAt,
            String status,
            List<SaleResponseItem> items
    ) {
        public static SaleResponse from(Sale sale) {
            return new SaleResponse(
                    sale.getId(),
                    sale.getStore().getId(),
                    sale.getStore().getName(),
                    sale.getCreatedAt(),
                    sale.getStatus().name(),
                    sale.getItems().stream().map(SaleResponseItem::from).toList()
            );
        }
    }

    public record SaleResponseItem(
            Long productId,
            String productName,
            Integer quantity
    ) {
        public static SaleResponseItem from(SaleItem item) {
            return new SaleResponseItem(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity()
            );
        }
    }
}
