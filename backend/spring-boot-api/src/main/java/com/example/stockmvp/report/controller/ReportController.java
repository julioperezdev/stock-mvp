package com.example.stockmvp.report.controller;

import com.example.stockmvp.report.application.ReportDtos;
import com.example.stockmvp.report.application.ReportService;
import com.example.stockmvp.stock.application.StockDtos;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily-stock")
    public ReportDtos.DailyStockReportResponse getDailyStockReport() {
        return reportService.getDailyStockReport();
    }

    @GetMapping("/low-stock")
    public List<ReportDtos.LowStockReportItemResponse> getLowStockReport() {
        return reportService.getLowStockReport();
    }

    @GetMapping("/stock-by-store/{storeId}")
    public List<StockDtos.StockResponse> getStockByStore(@PathVariable Long storeId) {
        return reportService.getStockByStore(storeId);
    }
}
