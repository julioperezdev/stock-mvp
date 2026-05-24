CREATE TABLE app_users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

INSERT INTO app_users (email, password_hash, full_name, role)
VALUES
    ('admin@stock.local', '{noop}admin123', 'Admin Stock', 'ADMIN'),
    ('local@stock.local', '{noop}local123', 'Usuario Local', 'STORE_USER'),
    ('fabrica@stock.local', '{noop}fabrica123', 'Usuario Fabrica', 'FACTORY_USER');
