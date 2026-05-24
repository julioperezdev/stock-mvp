package com.example.stockmvp.sale.infrastructure;

import com.example.stockmvp.sale.domain.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
}
