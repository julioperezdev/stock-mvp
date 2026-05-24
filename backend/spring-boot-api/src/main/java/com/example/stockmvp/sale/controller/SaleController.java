package com.example.stockmvp.sale.controller;

import com.example.stockmvp.sale.application.SaleDtos;
import com.example.stockmvp.sale.application.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SaleDtos.SaleResponse createSale(@Valid @RequestBody SaleDtos.CreateSaleRequest request) {
        return saleService.createSale(request);
    }

    @GetMapping
    public List<SaleDtos.SaleResponse> listSales() {
        return saleService.listSales();
    }

    @GetMapping("/{id}")
    public SaleDtos.SaleResponse getSale(@PathVariable Long id) {
        return saleService.getSale(id);
    }
}
