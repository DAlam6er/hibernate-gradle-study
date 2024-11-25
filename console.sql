-- Таблица users v2 для проекта
ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS role INT;

-- Таблица users v2 для проекта
ALTER TABLE IF EXISTS users
    ALTER COLUMN role TYPE VARCHAR(32);

UPDATE users
SET role = 'USER'
WHERE role = '0';

UPDATE users
SET role = 'ADMIN'
WHERE role = '1';

-- Таблица users v3 для проекта
ALTER TABLE IF EXISTS users
    DROP COLUMN IF EXISTS age;

-- Таблица users v5 для проекта
ALTER TABLE IF EXISTS users
    ADD COLUMN info jsonb;

-- Таблица, содержащая синтетический автогенерируемый ключ, использующий стратегию IDENTITY
CREATE TABLE IF NOT EXISTS users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(128) UNIQUE,
    firstname  VARCHAR(128),
    lastname   VARCHAR(128),
    birth_date DATE,
    role       VARCHAR(32),
    info       JSONB
);

-- Таблица, содержащая синтетический ключ, создаваемый вручную и использующий стратегию SEQUENCE или TABLE
CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT PRIMARY KEY,
    username   VARCHAR(128) UNIQUE,
    firstname  VARCHAR(128),
    lastname   VARCHAR(128),
    birth_date DATE,
    role       VARCHAR(32),
    info       JSONB
);

-- Таблица, содержащая составной первичный ключ
CREATE TABLE IF NOT EXISTS users
(
    firstname  VARCHAR(128) NOT NULL,
    lastname   VARCHAR(128) NOT NULL,
    birth_date DATE         NOT NULL,
    username   VARCHAR(128) UNIQUE,
    role       VARCHAR(32),
    info       JSONB,
    PRIMARY KEY (firstname, lastname, birth_date)
);

DROP TABLE IF EXISTS users;

-- В случае использования стратегии автогенерирования SEQUENCE
CREATE SEQUENCE IF NOT EXISTS users_id_seq
    OWNED BY public.users.id;

DROP SEQUENCE IF EXISTS users_id_seq;

-- В случае использования стратегии автогенерирования TABLE
CREATE TABLE IF NOT EXISTS all_sequence
(
    table_name VARCHAR(32) PRIMARY KEY,
    pk_value   BIGINT NOT NULL
);