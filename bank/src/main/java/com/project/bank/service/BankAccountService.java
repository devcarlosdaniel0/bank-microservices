package com.project.bank.service;

import com.project.bank.dto.UpdateBalanceDTO;
import com.project.bank.dto.BankAccountResponseDTO;
import com.project.bank.dto.CreateBankAccountDTO;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserEntityRepository userEntityRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public BankAccountResponseDTO createBankAccount(CreateBankAccountDTO dto) {
        UserEntity user = userEntityRepository.findById(dto.userId())
                .orElseThrow(() -> new UserIdNotFoundException("User ID: " + dto.userId() + " not found"));

        if (user.getBankAccount() != null) {
            throw new UserAlreadyHasBankAccountException("User already has a bank account");
        }

        Long userIdFromToken = getUserIdFromToken();
        verifyUserIdMatch(userIdFromToken, dto.userId());

        BankAccount bankAccount = BankAccount.builder()
                .accountName(user.getUsername())
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();

        bankAccountRepository.save(bankAccount);

        return modelMapper.map(bankAccount, BankAccountResponseDTO.class);
    }

    @Transactional
    public BankAccountResponseDTO addBalance(UpdateBalanceDTO updateBalanceDTO) {
        BankAccount bankAccount = getBankAccountById(updateBalanceDTO.accountId());

        Long userId = bankAccount.getUser().getId();
        Long userIdFromToken = getUserIdFromToken();

        verifyUserIdMatch(userIdFromToken, userId);

        return updateBalance(updateBalanceDTO, Operation.ADD);
    }

    @Transactional
    public BankAccountResponseDTO withdrawalBalance(UpdateBalanceDTO updateBalanceDTO) {
        BankAccount bankAccount = getBankAccountById(updateBalanceDTO.accountId());

        Long userId = bankAccount.getUser().getId();
        Long userIdFromToken = getUserIdFromToken();

        verifyUserIdMatch(userIdFromToken, userId);

        return updateBalance(updateBalanceDTO, Operation.SUBTRACT);
    }

    private BankAccountResponseDTO updateBalance(UpdateBalanceDTO updateBalanceDTO, Operation operation) {
        BankAccount bankAccount = getBankAccountById(updateBalanceDTO.accountId());

        BigDecimal newBalance = operation == Operation.ADD
                ? bankAccount.getBalance().add(updateBalanceDTO.value())
                : bankAccount.getBalance().subtract(updateBalanceDTO.value());

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient funds for the withdrawal, you have: " + bankAccount.getBalance() + " you want to withdrawal: " + updateBalanceDTO.value());
        }

        bankAccount.setBalance(newBalance);

        bankAccountRepository.save(bankAccount);

        return modelMapper.map(bankAccount, BankAccountResponseDTO.class);
    }

    private enum Operation {
        ADD, SUBTRACT
    }

    public List<BankAccountResponseDTO> findAll() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();

        return bankAccounts.stream()
                .map(account -> modelMapper.map(account, BankAccountResponseDTO.class))
                .toList();
    }

    private BankAccount getBankAccountById(UUID accountId) {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountIdNotFoundException("The bank account id: " + accountId + " was not found"));
    }

    private void verifyUserIdMatch(Long userIdFromToken, Long userIdFromDto) {
        if (!userIdFromToken.equals(userIdFromDto)) {
            throw new UnauthorizedUserException("The user ID in the token does not match the requested user ID, userIdFromToken: " + userIdFromToken + " userIdFromDto: " + userIdFromDto);
        }
    }

    public Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getDetails();
    }
}
