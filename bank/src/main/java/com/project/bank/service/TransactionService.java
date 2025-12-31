package com.project.bank.service;

import com.project.bank.clients.CurrencyConverterClient;
import com.project.bank.domain.AuthUser;
import com.project.bank.domain.BankAccount;
import com.project.bank.domain.TransactionEntity;
import com.project.bank.dto.CurrencyResponse;
import com.project.bank.dto.TransferDTO;
import com.project.bank.dto.TransferResponseDTO;
import com.project.bank.exception.BankAccountNotFoundException;
import com.project.bank.exception.InsufficientFundsException;
import com.project.bank.exception.TransferNotAllowedException;
import com.project.bank.repository.BankAccountRepository;
import com.project.bank.repository.TransactionEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final BankAccountRepository bankAccountRepository;
    private final CurrencyConverterClient currencyConverterClient;
    private final TransactionEntityRepository transactionEntityRepository;

    @Transactional
    public TransferResponseDTO transfer(TransferDTO transferDTO) {
        if (transferDTO.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferNotAllowedException("Transfer value must be greater than zero.");
        }

        Long userIdFromToken = getUserIdFromToken();

        BankAccount sender = getBankAccountFromUserId(userIdFromToken);
        BankAccount receiver = bankAccountRepository.findByAccountEmail(transferDTO.receiverAccountEmail())
                .orElseThrow(() -> new RuntimeException(String.format("Email: %s was not found", transferDTO.receiverAccountEmail())));

        if (transferDTO.receiverAccountEmail().equalsIgnoreCase(sender.getAccountEmail())) {
            throw new TransferNotAllowedException("You cant transfer to your own bank account");
        }

        if (sender.getBalance().compareTo(transferDTO.value()) < 0) {
            throw new InsufficientFundsException(String.format(
                    "Insufficient funds. Current balance is %s, attempted transfer: %s",
                    sender.getBalance(), transferDTO.value()
            ));
        }

        return sender.getCurrency().equals(receiver.getCurrency())
                ? processSameCurrencyTransfer(sender, receiver, transferDTO.value())
                : processDifferentCurrencyTransfer(sender, receiver, transferDTO.value());
    }

    private TransferResponseDTO processSameCurrencyTransfer(BankAccount sender, BankAccount receiver, BigDecimal transferValue) {
        updateBalances(sender, receiver, transferValue, transferValue);
        TransactionEntity transactionEntity = buildTransaction(sender, receiver, transferValue, null);
        transactionEntityRepository.save(transactionEntity);

        return buildTransferResponseDTO(sender, receiver, transferValue, null);
    }

    private TransferResponseDTO processDifferentCurrencyTransfer(BankAccount sender, BankAccount receiver, BigDecimal transferValue) {
        BigDecimal convertedAmount = getConvertedAmount(transferValue, sender.getCurrency().getCurrencyCode(), receiver.getCurrency().getCurrencyCode());
        updateBalances(sender, receiver, transferValue, convertedAmount);
        TransactionEntity transactionEntity = buildTransaction(sender, receiver, transferValue, convertedAmount);
        transactionEntityRepository.save(transactionEntity);

        return buildTransferResponseDTO(sender, receiver, transferValue, convertedAmount);
    }

    private void updateBalances(BankAccount sender, BankAccount receiver, BigDecimal amountToSubtract, BigDecimal amountToAdd) {
        sender.setBalance(sender.getBalance().subtract(amountToSubtract));
        receiver.setBalance(receiver.getBalance().add(amountToAdd));
    }

    private BigDecimal getConvertedAmount(BigDecimal transferValue, String senderCurrency, String receiverCurrency) {
        CurrencyResponse currencyResponse = currencyConverterClient.convertCurrencies(transferValue,
                String.format("%s_%s", senderCurrency, receiverCurrency));

        return currencyResponse.convertedAmount();
    }

    private TransactionEntity buildTransaction(BankAccount sender, BankAccount receiver, BigDecimal transferValue, BigDecimal convertedAmount) {
        return TransactionEntity.builder()
                .senderID(sender.getId().toString())
                .senderEmail(sender.getAccountEmail())
                .senderName(sender.getAccountName())
                .senderCurrency(sender.getCurrency().getCurrencyCode())
                .transferValue(transferValue)
                .receiverID(receiver.getId().toString())
                .receiverEmail(receiver.getAccountEmail())
                .receiverName(receiver.getAccountName())
                .receiverCurrency(receiver.getCurrency().getCurrencyCode())
                .convertedAmount(convertedAmount)
                .timestamp(LocalDateTime.now().atZone(ZoneOffset.UTC).toLocalDateTime())
                .build();
    }

    private TransferResponseDTO buildTransferResponseDTO(BankAccount sender, BankAccount receiver, BigDecimal transferValue, BigDecimal convertedAmount) {
        return TransferResponseDTO.builder()
                .senderCurrentBalance(sender.getBalance())
                .senderCurrencyCode(sender.getCurrency().getCurrencyCode())
                .transferredValue(transferValue)
                .receiverCurrencyCode(receiver.getCurrency().getCurrencyCode())
                .receiverName(receiver.getAccountName())
                .receiverEmail(receiver.getAccountEmail())
                .convertedAmount(convertedAmount)
                .build();
    }

    private Long getUserIdFromToken() {
        AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return authUser.id();
    }

    private BankAccount getBankAccountFromUserId(Long userId) {
        return bankAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new BankAccountNotFoundException("User does not have a bank account"));
    }
}
