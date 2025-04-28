package com.project.bank.service;

import com.project.bank.dto.BankAccountFoundDTO;
import com.project.bank.dto.BankAccountResponseDTO;
import com.project.bank.dto.CreateBankAccountDTO;
import com.project.bank.dto.UpdateBalanceDTO;
import com.project.bank.exception.*;
import com.project.core.domain.BankAccount;
import com.project.core.domain.UserEntity;
import com.project.core.repository.BankAccountRepository;
import com.project.core.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserEntityRepository userEntityRepository;
    private final ModelMapper modelMapper;

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

    public Page<BankAccountResponseDTO> findAll(Pageable pageable) {
        Page<BankAccount> bankAccounts = bankAccountRepository.findAll(pageable);

        return bankAccounts.map(account -> modelMapper.map(account, BankAccountResponseDTO.class));
    }

    public BankAccountFoundDTO findBankAccountIdByAccountEmail(String accountEmail) {
        BankAccount bankAccount = bankAccountRepository.findByAccountEmail(accountEmail)
                .orElseThrow(() -> new BankAccountIdNotFoundException("The bank account email: '" + accountEmail + "' was not found"));

        return new BankAccountFoundDTO(bankAccount.getId());
    }

    public BigDecimal checkBalance() {
        Long userIdFromToken = getUserIdFromToken();

        UserEntity user = getUserByUserId(userIdFromToken);
        BankAccount bankAccount = getBankAccountFromUser(user);

        return bankAccount.getBalance();
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
