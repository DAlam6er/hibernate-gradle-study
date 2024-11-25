-- Таблица users v1 для проекта
CREATE TABLE IF NOT EXISTS users_v1
(
    username   VARCHAR(128) PRIMARY KEY,
    firstname  VARCHAR(128),
    lastname   VARCHAR(128),
    birth_date DATE,
    age        INT
);