CREATE TABLE stocks (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    current_quantity INTEGER NOT NULL,
    minimum_quantity INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_stocks_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_stocks_store FOREIGN KEY (store_id) REFERENCES stores(id),
    CONSTRAINT uk_stocks_product_store UNIQUE (product_id, store_id),
    CONSTRAINT chk_stock_current_quantity CHECK (current_quantity >= 0),
    CONSTRAINT chk_stock_minimum_quantity CHECK (minimum_quantity >= 0)
);
