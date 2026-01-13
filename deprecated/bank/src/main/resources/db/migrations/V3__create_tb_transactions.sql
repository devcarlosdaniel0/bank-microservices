CREATE TABLE tb_transactions (
  id BINARY(16) NOT NULL,
  senderid VARCHAR(255) DEFAULT NULL,
  receiverid VARCHAR(255) DEFAULT NULL,
  sender_currency VARCHAR(255) DEFAULT NULL,
  receiver_currency VARCHAR(255) DEFAULT NULL,
  transfer_value DECIMAL(38,2) DEFAULT NULL,
  converted_amount DECIMAL(38,2) DEFAULT NULL,
  timestamp DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
