package com.example.stockmvp.report.application;

import com.example.stockmvp.product.infrastructure.ProductRepository;
import com.example.stockmvp.stock.application.StockDtos;
import com.example.stockmvp.stock.application.StockService;
import com.example.stockmvp.store.infrastructure.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final StockService stockService;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    public ReportService(StockService stockService, ProductRepository productRepository, StoreRepository storeRepository) {
        this.stockService = stockService;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public ReportDtos.DailyStockReportResponse getDailyStockReport() {
        List<StockDtos.StockResponse> items = stockService.listStocks(null, null);
        long criticalItems = items.stream().filter(StockDtos.StockResponse::critical).count();

        log.info("event=daily_stock_report_generated totalItems={} criticalItems={}", items.size(), criticalItems);

        return new ReportDtos.DailyStockReportResponse(
                productRepository.findByActiveTrueOrderByNameAsc().size(),
                storeRepository.findByActiveTrueOrderByNameAsc().size(),
                items.size(),
                Math.toIntExact(criticalItems),
                LocalDateTime.now(),
                items
        );
    }

    @Transactional(readOnly = true)
    public List<ReportDtos.LowStockReportItemResponse> getLowStockReport() {
        List<ReportDtos.LowStockReportItemResponse> items = stockService.listLowStock()
                .stream()
                .map(ReportDtos.LowStockReportItemResponse::from)
                .filter(item -> item.suggestedReplenishment() > 0)
                .toList();

        log.info("event=low_stock_report_generated totalItems={}", items.size());
        return items;
    }

    @Transactional(readOnly = true)
    public List<StockDtos.StockResponse> getStockByStore(Long storeId) {
        return stockService.listStocks(storeId, null);
    }
}
