INSERT INTO app_users (email, password_hash, full_name, role)
VALUES
    (
        'admin@stock.local',
        '{bcrypt}$2a$10$I0X3SuV1avvHa8RbEhco/uPUS1G.jLUCwzgHiPmndSQvxVA9yvDnC',
        'Admin Stock',
        'ADMIN'
    ),
    (
        'local@stock.local',
        '{bcrypt}$2a$10$uwk.4Kaet0bby/5shezrReHvYx0QOSA0E1hgQ5URCWIawP/6sZ.LW',
        'Usuario Local',
        'STORE_USER'
    ),
    (
        'fabrica@stock.local',
        '{bcrypt}$2a$10$Mb/RuobDsSWnVtmmg8R6weQVPfvLV.aqoRMdavnPIG0d2ffngvfhm',
        'Usuario Fabrica',
        'FACTORY_USER'
    );
