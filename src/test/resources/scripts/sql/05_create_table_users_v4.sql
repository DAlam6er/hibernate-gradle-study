-- Таблица users v3 для проекта
CREATE TABLE IF NOT EXISTS users_v4
(
    username   VARCHAR(128) PRIMARY KEY,
    firstname  VARCHAR(128),
    lastname   VARCHAR(128),
    birth_date DATE,
    role       VARCHAR(8),
    info       JSONB
);