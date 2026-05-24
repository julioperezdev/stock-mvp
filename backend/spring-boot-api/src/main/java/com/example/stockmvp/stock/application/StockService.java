package com.example.stockmvp.stock.application;

import com.example.stockmvp.product.application.ProductService;
import com.example.stockmvp.product.domain.Product;
import com.example.stockmvp.shared.error.DuplicateResourceException;
import com.example.stockmvp.shared.error.ResourceNotFoundException;
import com.example.stockmvp.stock.domain.Stock;
import com.example.stockmvp.stock.infrastructure.StockRepository;
import com.example.stockmvp.store.application.StoreService;
import com.example.stockmvp.store.domain.Store;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final ProductService productService;
    private final StoreService storeService;

    public StockService(StockRepository stockRepository, ProductService productService, StoreService storeService) {
        this.stockRepository = stockRepository;
        this.productService = productService;
        this.storeService = storeService;
    }

    @Transactional(readOnly = true)
    public List<StockDtos.StockResponse> listStocks(Long storeId, Long productId) {
        List<Stock> stocks;
        if (storeId != null) {
            stocks = stockRepository.findByStoreIdOrderByProductNameAsc(storeId);
        } else if (productId != null) {
            stocks = stockRepository.findByProductIdOrderByStoreNameAsc(productId);
        } else {
            stocks = stockRepository.findAllForResponse();
        }

        return stocks.stream().map(StockDtos.StockResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public StockDtos.StockResponse getStock(Long id) {
        return StockDtos.StockResponse.from(findStock(id));
    }

    @Transactional(readOnly = true)
    public List<StockDtos.StockResponse> listLowStock() {
        return stockRepository.findLowStock()
                .stream()
                .map(StockDtos.StockResponse::from)
                .toList();
    }

    @Transactional
    public StockDtos.StockResponse createStock(StockDtos.CreateStockRequest request) {
        if (stockRepository.existsByProductIdAndStoreId(request.productId(), request.storeId())) {
            throw new DuplicateResourceException("Stock already exists for product and store");
        }

        Product product = productService.findActiveProduct(request.productId());
        Store store = storeService.findActiveStore(request.storeId());
        Stock stock = stockRepository.save(new Stock(product, store, request.currentQuantity(), request.minimumQuantity()));
        return StockDtos.StockResponse.from(stock);
    }

    @Transactional
    public StockDtos.StockResponse updateStock(Long id, StockDtos.UpdateStockRequest request) {
        Stock stock = findStock(id);
        stock.updateQuantities(request.currentQuantity(), request.minimumQuantity());
        return StockDtos.StockResponse.from(stock);
    }

    public Stock findStockByProductAndStore(Long productId, Long storeId) {
        return stockRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
    }

    private Stock findStock(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
    }
}
