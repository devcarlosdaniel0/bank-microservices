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

    private String senderEmail;

    private String senderName;

    private String senderCurrency;

    private BigDecimal transferValue;

    private String receiverID;

    private String receiverEmail;

    private String receiverName;

    private String receiverCurrency;

    private BigDecimal convertedAmount;

    private LocalDateTime timestamp;
}
