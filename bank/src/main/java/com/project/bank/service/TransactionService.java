package com.project.bank.service;

import com.project.auth.security.exception.EmailNotFoundException;
import com.project.bank.clients.CurrencyConverterClient;
import com.project.bank.dto.CurrencyResponse;
import com.project.bank.dto.TransferDTO;
import com.project.bank.dto.TransferResponseDTO;
import com.project.bank.exception.BankAccountNotFoundException;
import com.project.bank.exception.InsufficientFundsException;
import com.project.bank.exception.TransferNotAllowedException;
import com.project.bank.exception.UserIdNotFoundException;
import com.project.core.domain.BankAccount;
import com.project.core.domain.UserEntity;
import com.project.core.repository.BankAccountRepository;
import com.project.core.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final BankAccountRepository bankAccountRepository;
    private final UserEntityRepository userEntityRepository;
    private final CurrencyConverterClient currencyConverterClient;

    @Transactional
    public TransferResponseDTO transfer(TransferDTO transferDTO) {
        if (transferDTO.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferNotAllowedException("Transfer value must be greater than zero.");
        }

        Long userIdFromToken = getUserIdFromToken();
        UserEntity userFromToken = getUserByUserId(userIdFromToken);

        BankAccount sender = getBankAccountFromUser(userFromToken);
        BankAccount receiver = bankAccountRepository.findByAccountEmail(transferDTO.receiverAccountEmail())
                .orElseThrow(() -> new EmailNotFoundException(String.format("Email: %s was not found", transferDTO.receiverAccountEmail())));

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
                ? processSameCurrencyTransfer(sender, receiver, transferDTO)
                : processDifferentCurrencyTransfer(sender, receiver, transferDTO);
    }

    private TransferResponseDTO processSameCurrencyTransfer(BankAccount sender, BankAccount receiver, TransferDTO transferDTO) {
        sender.setBalance(sender.getBalance().subtract(transferDTO.value()));
        receiver.setBalance(receiver.getBalance().add(transferDTO.value()));

        return TransferResponseDTO.builder()
                .senderCurrentBalance(sender.getBalance())
                .senderCurrencyCode(sender.getCurrency().getCurrencyCode())
                .transferredValue(transferDTO.value())
                .receiverCurrencyCode(receiver.getCurrency().getCurrencyCode())
                .receiverName(receiver.getAccountName())
                .receiverEmail(receiver.getAccountEmail())
                .build();
    }

    private TransferResponseDTO processDifferentCurrencyTransfer(BankAccount sender, BankAccount receiver, TransferDTO transferDTO) {
        String senderCurrency = sender.getCurrency().getCurrencyCode();
        String receiverCurrency = receiver.getCurrency().getCurrencyCode();

        CurrencyResponse currencyResponse = currencyConverterClient.convertCurrencies(transferDTO.value(),
                String.format("%s_%s", senderCurrency, receiverCurrency));

        BigDecimal convertedAmount = currencyResponse.convertedAmount();

        sender.setBalance(sender.getBalance().subtract(transferDTO.value()));
        receiver.setBalance(receiver.getBalance().add(convertedAmount));

        return TransferResponseDTO.builder()
                .senderCurrentBalance(sender.getBalance())
                .senderCurrencyCode(senderCurrency)
                .transferredValue(transferDTO.value())
                .receiverName(receiver.getAccountName())
                .receiverEmail(receiver.getAccountEmail())
                .convertedAmount(convertedAmount)
                .receiverCurrencyCode(receiverCurrency)
                .build();
    }

    private Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getDetails();
    }

    private UserEntity getUserByUserId(Long userIdFromToken) {
        return userEntityRepository.findById(userIdFromToken)
                .orElseThrow(() -> new UserIdNotFoundException("User ID: " + userIdFromToken + " not found"));
    }

    private BankAccount getBankAccountFromUser(UserEntity user) {
        BankAccount bankAccount = user.getBankAccount();
        if (bankAccount == null) {
            throw new BankAccountNotFoundException("User does not have a bank account");
        }
        return bankAccount;
    }
}
