package com.example.stockmvp.sale.application;

import com.example.stockmvp.product.application.ProductService;
import com.example.stockmvp.product.domain.Product;
import com.example.stockmvp.sale.domain.Sale;
import com.example.stockmvp.sale.domain.SaleItem;
import com.example.stockmvp.sale.domain.StockMovement;
import com.example.stockmvp.sale.domain.StockMovementType;
import com.example.stockmvp.sale.infrastructure.SaleRepository;
import com.example.stockmvp.sale.infrastructure.StockMovementRepository;
import com.example.stockmvp.shared.error.InsufficientStockException;
import com.example.stockmvp.shared.error.ResourceNotFoundException;
import com.example.stockmvp.stock.application.StockService;
import com.example.stockmvp.stock.domain.Stock;
import com.example.stockmvp.store.application.StoreService;
import com.example.stockmvp.store.domain.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SaleService {

    private static final Logger log = LoggerFactory.getLogger(SaleService.class);

    private final SaleRepository saleRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StoreService storeService;
    private final ProductService productService;
    private final StockService stockService;

    public SaleService(
            SaleRepository saleRepository,
            StockMovementRepository stockMovementRepository,
            StoreService storeService,
            ProductService productService,
            StockService stockService
    ) {
        this.saleRepository = saleRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.storeService = storeService;
        this.productService = productService;
        this.stockService = stockService;
    }

    @Transactional
    public SaleDtos.SaleResponse createSale(SaleDtos.CreateSaleRequest request) {
        log.info("event=sale_create_started storeId={} items={}", request.storeId(), request.items().size());

        Store store = storeService.findActiveStore(request.storeId());
        Map<Long, Integer> quantitiesByProduct = aggregateQuantities(request.items());

        Sale sale = new Sale(store, request.createdBy());
        List<PendingMovement> pendingMovements = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : quantitiesByProduct.entrySet()) {
            Long productId = entry.getKey();
            Integer requestedQuantity = entry.getValue();
            Product product = productService.findActiveProduct(productId);
            Stock stock = stockService.findStockByProductAndStore(productId, store.getId());

            if (stock.getCurrentQuantity() < requestedQuantity) {
                log.info(
                        "event=insufficient_stock storeId={} productId={} requestedQuantity={} availableQuantity={}",
                        store.getId(),
                        productId,
                        requestedQuantity,
                        stock.getCurrentQuantity()
                );
                throw new InsufficientStockException(productId, requestedQuantity, stock.getCurrentQuantity());
            }

            int previousQuantity = stock.getCurrentQuantity();
            stock.decrease(requestedQuantity);
            int newQuantity = stock.getCurrentQuantity();

            sale.addItem(new SaleItem(product, requestedQuantity));
            pendingMovements.add(new PendingMovement(
                    product,
                    requestedQuantity,
                    previousQuantity,
                    newQuantity
            ));
        }

        Sale savedSale = saleRepository.save(sale);
        pendingMovements.forEach(movement -> stockMovementRepository.save(new StockMovement(
                movement.product(),
                store,
                savedSale,
                StockMovementType.SALE,
                movement.quantity(),
                movement.previousQuantity(),
                movement.newQuantity(),
                request.createdBy()
        )));

        log.info("event=sale_created saleId={} storeId={} status={} items={}",
                savedSale.getId(),
                store.getId(),
                savedSale.getStatus(),
                savedSale.getItems().size()
        );

        return SaleDtos.SaleResponse.from(savedSale);
    }

    @Transactional(readOnly = true)
    public List<SaleDtos.SaleResponse> listSales() {
        return saleRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(SaleDtos.SaleResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SaleDtos.SaleResponse getSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        return SaleDtos.SaleResponse.from(sale);
    }

    private Map<Long, Integer> aggregateQuantities(List<SaleDtos.CreateSaleItemRequest> items) {
        Map<Long, Integer> quantitiesByProduct = new LinkedHashMap<>();
        for (SaleDtos.CreateSaleItemRequest item : items) {
            quantitiesByProduct.merge(item.productId(), item.quantity(), Integer::sum);
        }
        return quantitiesByProduct;
    }

    private record PendingMovement(Product product, int quantity, int previousQuantity, int newQuantity) {
    }
}
