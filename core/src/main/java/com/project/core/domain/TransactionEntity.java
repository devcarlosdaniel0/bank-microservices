package com.project.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String senderID;

    private String receiverID;

    private String senderCurrency;

    private String receiverCurrency;

    private BigDecimal transferValue;

    private BigDecimal convertedAmount;

    private LocalDateTime timestamp;
}
