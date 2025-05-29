CREATE TABLE tb_bank_account (
  id BINARY(16) NOT NULL,
  account_email VARCHAR(255) NOT NULL,
  account_name VARCHAR(255) NOT NULL,
  balance DECIMAL(38,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  user_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UKg02uncwapgttt966g99a6ogfc (user_id),
  CONSTRAINT FKae9cjhbcubpi8ffdhhjipt1hp FOREIGN KEY (user_id) REFERENCES tb_users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
