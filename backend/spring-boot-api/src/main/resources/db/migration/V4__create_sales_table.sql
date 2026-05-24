CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    CONSTRAINT fk_sales_store FOREIGN KEY (store_id) REFERENCES stores(id)
);
