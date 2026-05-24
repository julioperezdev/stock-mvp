package com.example.stockmvp.stock.infrastructure;

import com.example.stockmvp.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("""
            select s from Stock s
            join fetch s.product p
            join fetch s.store st
            where st.id = :storeId
            order by p.name asc
            """)
    List<Stock> findByStoreIdOrderByProductNameAsc(@Param("storeId") Long storeId);

    @Query("""
            select s from Stock s
            join fetch s.product p
            join fetch s.store st
            where p.id = :productId
            order by st.name asc
            """)
    List<Stock> findByProductIdOrderByStoreNameAsc(@Param("productId") Long productId);

    Optional<Stock> findByProductIdAndStoreId(Long productId, Long storeId);

    boolean existsByProductIdAndStoreId(Long productId, Long storeId);

    @Query("""
            select s from Stock s
            join fetch s.product p
            join fetch s.store st
            order by st.name asc, p.name asc
            """)
    List<Stock> findAllForResponse();

    @Query("""
            select s from Stock s
            join fetch s.product p
            join fetch s.store st
            where s.currentQuantity <= s.minimumQuantity
            order by st.name asc, p.name asc
            """)
    List<Stock> findLowStock();
}
