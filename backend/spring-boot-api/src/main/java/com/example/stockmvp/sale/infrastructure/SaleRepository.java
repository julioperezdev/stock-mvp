package com.example.stockmvp.sale.infrastructure;

import com.example.stockmvp.sale.domain.Sale;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @EntityGraph(attributePaths = {"store", "items", "items.product"})
    List<Sale> findAllByOrderByCreatedAtDesc();

    @Override
    @EntityGraph(attributePaths = {"store", "items", "items.product"})
    Optional<Sale> findById(Long id);
}
