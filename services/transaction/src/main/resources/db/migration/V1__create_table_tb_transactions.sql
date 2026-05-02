CREATE TABLE tb_transaction
(
    id                VARCHAR(255) NOT NULL,
    type              VARCHAR(50)  NOT NULL,
    source_amount     DECIMAL(38, 2) DEFAULT NULL,
    target_amount     DECIMAL(38, 2) DEFAULT NULL,
    source_currency   VARCHAR(3)     DEFAULT NULL,
    target_currency   VARCHAR(3)     DEFAULT NULL,
    exchange_rate     DECIMAL(18, 8) DEFAULT NULL,
    source_account_id VARCHAR(255)   DEFAULT NULL,
    target_account_id VARCHAR(255)   DEFAULT NULL,
    source_email      VARCHAR(255)   DEFAULT NULL,
    target_email      VARCHAR(255)   DEFAULT NULL,
    time_stamp        DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    INDEX             IDX_tx_source_account (source_account_id),
    INDEX             IDX_tx_target_account (target_account_id),
    INDEX             IDX_tx_timestamp (time_stamp)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
