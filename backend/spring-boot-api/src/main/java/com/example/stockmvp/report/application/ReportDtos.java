package com.example.stockmvp.report.application;

import com.example.stockmvp.stock.application.StockDtos;

import java.time.LocalDateTime;
import java.util.List;

public final class ReportDtos {

    private ReportDtos() {
    }

    public record DailyStockReportResponse(
            int totalProducts,
            int totalStores,
            int totalStockItems,
            int criticalItems,
            LocalDateTime generatedAt,
            List<StockDtos.StockResponse> items
    ) {
    }

    public record LowStockReportItemResponse(
            Long stockId,
            Long productId,
            String productName,
            Long storeId,
            String storeName,
            Integer currentQuantity,
            Integer minimumQuantity,
            Integer suggestedReplenishment
    ) {
        public static LowStockReportItemResponse from(StockDtos.StockResponse stock) {
            int suggested = Math.max(0, stock.minimumQuantity() - stock.currentQuantity());
            return new LowStockReportItemResponse(
                    stock.stockId(),
                    stock.productId(),
                    stock.productName(),
                    stock.storeId(),
                    stock.storeName(),
                    stock.currentQuantity(),
                    stock.minimumQuantity(),
                    suggested
            );
        }
    }
}
