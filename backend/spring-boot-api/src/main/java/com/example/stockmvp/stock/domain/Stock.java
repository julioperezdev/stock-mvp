package com.example.stockmvp.stock.domain;

import com.example.stockmvp.product.domain.Product;
import com.example.stockmvp.store.domain.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "stocks",
        uniqueConstraints = @UniqueConstraint(name = "uk_stocks_product_store", columnNames = {"product_id", "store_id"})
)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private int currentQuantity;

    @Column(nullable = false)
    private int minimumQuantity;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Stock() {
    }

    public Stock(Product product, Store store, int currentQuantity, int minimumQuantity) {
        this.product = product;
        this.store = store;
        this.currentQuantity = currentQuantity;
        this.minimumQuantity = minimumQuantity;
    }

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Store getStore() {
        return store;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isCritical() {
        return currentQuantity <= minimumQuantity;
    }

    public void updateQuantities(int currentQuantity, int minimumQuantity) {
        this.currentQuantity = currentQuantity;
        this.minimumQuantity = minimumQuantity;
    }

    public void decrease(int quantity) {
        this.currentQuantity -= quantity;
    }
}
