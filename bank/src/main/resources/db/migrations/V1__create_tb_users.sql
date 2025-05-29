CREATE TABLE tb_users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  confirmation_token VARCHAR(255) DEFAULT NULL,
  confirmation_token_expiration DATETIME(6) DEFAULT NULL,
  email VARCHAR(255) NOT NULL,
  is_confirmed BIT(1) NOT NULL,
  password VARCHAR(255) NOT NULL,
  roles ENUM('ADMIN', 'USER') NOT NULL,
  username VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UKgrd22228p1miaivbn9yg178pm (email)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
