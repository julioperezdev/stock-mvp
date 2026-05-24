package com.example.stockmvp.product.application;

import com.example.stockmvp.product.domain.Product;
import com.example.stockmvp.product.infrastructure.ProductRepository;
import com.example.stockmvp.shared.error.DuplicateResourceException;
import com.example.stockmvp.shared.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductDtos.ProductResponse> listProducts() {
        return productRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(ProductDtos.ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDtos.ProductResponse getProduct(Long id) {
        return ProductDtos.ProductResponse.from(findProduct(id));
    }

    @Transactional
    public ProductDtos.ProductResponse createProduct(ProductDtos.CreateProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product sku already exists");
        }
        Product product = productRepository.save(new Product(request.sku(), request.name(), request.description()));
        return ProductDtos.ProductResponse.from(product);
    }

    @Transactional
    public ProductDtos.ProductResponse updateProduct(Long id, ProductDtos.UpdateProductRequest request) {
        Product product = findProduct(id);
        productRepository.findBySku(request.sku())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Product sku already exists");
                });

        product.update(request.sku(), request.name(), request.description(), request.active());
        return ProductDtos.ProductResponse.from(product);
    }

    @Transactional
    public void deactivateProduct(Long id) {
        findProduct(id).deactivate();
    }

    public Product findActiveProduct(Long id) {
        Product product = findProduct(id);
        if (!product.isActive()) {
            throw new ResourceNotFoundException("Product not found");
        }
        return product;
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
