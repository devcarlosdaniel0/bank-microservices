package com.project.bank.service;

import com.project.auth.security.exception.EmailNotFoundException;
import com.project.bank.clients.CurrencyConverterClient;
import com.project.bank.dto.*;
import com.project.bank.exception.*;
import com.project.core.domain.BankAccount;
import com.project.core.domain.UserEntity;
import com.project.core.repository.BankAccountRepository;
import com.project.core.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserEntityRepository userEntityRepository;
    private final ModelMapper modelMapper;
    private final CurrencyConverterClient currencyConverterClient;

    @Transactional
    public BankAccountResponseDTO createBankAccount(CreateBankAccountDTO createBankAccountDTO) {
        Long userIdFromToken = getUserIdFromToken();

        UserEntity user = getUserByUserId(userIdFromToken);

        if (user.getBankAccount() != null) {
            throw new UserAlreadyHasBankAccountException("User already has a bank account");
        }

        if (!user.isConfirmed()) {
            throw new UnconfirmedUserException("Your user are not confirmed! Please confirm your account");
        }

        Currency currency = getCurrencyByCurrencyCode(createBankAccountDTO.currencyCode());

        BankAccount bankAccount = BankAccount.builder()
                .user(user)
                .accountEmail(user.getEmail())
                .accountName(user.getUsername())
                .balance(BigDecimal.ZERO)
                .currency(currency)
                .build();

        bankAccountRepository.save(bankAccount);

        return modelMapper.map(bankAccount, BankAccountResponseDTO.class);
    }

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

        if(!sender.getCurrency().equals(receiver.getCurrency())) {
            return processDifferentCurrencyTransfer(sender, receiver, transferDTO);
        } else {
            return processSameCurrencyTransfer(sender, receiver, transferDTO);
        }
    }

    public List<BankAccountResponseDTO> findAll() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();

        return bankAccounts.stream()
                .map(account -> modelMapper.map(account, BankAccountResponseDTO.class))
                .toList();
    }

    public BankAccountFoundDTO findBankAccountIdByAccountEmail(String accountEmail) {
        BankAccount bankAccount = bankAccountRepository.findByAccountEmail(accountEmail)
                .orElseThrow(() -> new BankAccountIdNotFoundException("The bank account email: '" + accountEmail + "' was not found"));

        return new BankAccountFoundDTO(bankAccount.getId());
    }

    @Transactional
    public BankAccountResponseDTO addBalance(UpdateBalanceDTO updateBalanceDTO) {
        Long userIdFromToken = getUserIdFromToken();

        UserEntity user = getUserByUserId(userIdFromToken);
        BankAccount bankAccount = getBankAccountFromUser(user);

        return updateBalance(bankAccount, updateBalanceDTO.value(), Operation.ADD);
    }

    @Transactional
    public BankAccountResponseDTO withdrawalBalance(UpdateBalanceDTO updateBalanceDTO) {
        Long userIdFromToken = getUserIdFromToken();

        UserEntity user = getUserByUserId(userIdFromToken);
        BankAccount bankAccount = getBankAccountFromUser(user);

        return updateBalance(bankAccount, updateBalanceDTO.value(), Operation.SUBTRACT);
    }

    private BankAccountResponseDTO updateBalance(BankAccount bankAccount, BigDecimal value, Operation operation) {
        BigDecimal newBalance = operation == Operation.ADD
                ? bankAccount.getBalance().add(value)
                : bankAccount.getBalance().subtract(value);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException(String.format(
                    "Insufficient funds. Current balance is %s, attempted withdrawal: %s",
                    bankAccount.getBalance(), value
            ));
        }

        bankAccount.setBalance(newBalance);

        bankAccountRepository.save(bankAccount);

        return modelMapper.map(bankAccount, BankAccountResponseDTO.class);
    }

    private enum Operation {
        ADD, SUBTRACT
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

    private Currency getCurrencyByCurrencyCode(String currencyCode) {
        try {
            return Currency.getInstance(currencyCode.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCurrencyCodeException("Example: BRL, USD, CAD, AUD");
        }
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
