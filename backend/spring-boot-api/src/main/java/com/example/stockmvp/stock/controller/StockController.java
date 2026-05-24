package com.example.stockmvp.stock.controller;

import com.example.stockmvp.stock.application.StockDtos;
import com.example.stockmvp.stock.application.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<StockDtos.StockResponse> listStocks(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Long productId
    ) {
        return stockService.listStocks(storeId, productId);
    }

    @GetMapping("/low-stock")
    public List<StockDtos.StockResponse> listLowStock() {
        return stockService.listLowStock();
    }

    @GetMapping("/{id}")
    public StockDtos.StockResponse getStock(@PathVariable Long id) {
        return stockService.getStock(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockDtos.StockResponse createStock(@Valid @RequestBody StockDtos.CreateStockRequest request) {
        return stockService.createStock(request);
    }

    @PutMapping("/{id}")
    public StockDtos.StockResponse updateStock(@PathVariable Long id, @Valid @RequestBody StockDtos.UpdateStockRequest request) {
        return stockService.updateStock(id, request);
    }
}
