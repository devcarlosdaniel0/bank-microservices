CREATE TABLE tb_account
(
    id                 VARCHAR(255)   NOT NULL,
    user_id            VARCHAR(255)   NOT NULL,
    currency_code      VARCHAR(3)     NOT NULL,
    balance_amount     DECIMAL(38, 2) NOT NULL,
    created_at         DATETIME(6) NOT NULL,
    last_modified_date DATETIME(6) NOT NULL,
    version            BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY UK_tb_account_user_id (user_id)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;