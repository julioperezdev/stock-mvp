package com.example.stockmvp.product.infrastructure;

import com.example.stockmvp.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrueOrderByNameAsc();

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);
}
