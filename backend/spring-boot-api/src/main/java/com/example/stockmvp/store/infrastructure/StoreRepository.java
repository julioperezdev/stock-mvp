package com.example.stockmvp.store.infrastructure;

import com.example.stockmvp.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findByActiveTrueOrderByNameAsc();

    Optional<Store> findByCode(String code);

    boolean existsByCode(String code);
}
