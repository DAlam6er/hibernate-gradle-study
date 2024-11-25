-- Таблица users v2 для проекта
CREATE TABLE IF NOT EXISTS users_v2
(
    username   VARCHAR(128) PRIMARY KEY,
    firstname  VARCHAR(128),
    lastname   VARCHAR(128),
    birth_date DATE,
    age        INT,
    role       VARCHAR(8) -- для enum не следует использовать ordinal
);