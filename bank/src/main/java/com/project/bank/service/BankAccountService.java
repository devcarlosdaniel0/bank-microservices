package com.project.bank.service;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public BankAccountResponseDTO createBankAccount() {
        Long userIdFromToken = getUserIdFromToken();

        UserEntity user = userEntityRepository.findById(userIdFromToken)
                .orElseThrow(() -> new UserIdNotFoundException("User ID: " + userIdFromToken + " not found"));

        if (user.getBankAccount() != null) {
            throw new UserAlreadyHasBankAccountException("User already has a bank account");
        }

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

    public List<BankAccountResponseDTO> findAll() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();

        return bankAccounts.stream()
                .map(account -> modelMapper.map(account, BankAccountResponseDTO.class))
                .toList();
    }

    public UserFoundedDTO findUserIdByUsername(String username) {
        UserEntity user = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));

        return new UserFoundedDTO(user.getId());
    }

    @Transactional
    public TransferResponseDTO transfer(TransferDTO transferDTO) {
        if (transferDTO.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferNotAllowedException("Transfer value must be greater than zero.");
        }

        Long userIdFromToken = getUserIdFromToken();
        UserEntity userFromToken = userEntityRepository.findById(userIdFromToken)
                .orElseThrow(() -> new UserIdNotFoundException("User ID: " + userIdFromToken + " not found"));

        BankAccount sender = userFromToken.getBankAccount();
        BankAccount receiver = getBankAccountById(transferDTO.receiverAccountId());

        if (sender.getBalance().compareTo(transferDTO.value()) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Current balance: " + sender.getBalance());
        }

        sender.setBalance(sender.getBalance().subtract(transferDTO.value()));
        receiver.setBalance(receiver.getBalance().add(transferDTO.value()));

        return TransferResponseDTO.builder().response(String.format("Your current balance is: %s and you transferred %s to account ID %s (%s)"
                , sender.getBalance(), transferDTO.value(), receiver.getId(), receiver.getAccountName())).build();
    }

    private BankAccountResponseDTO updateBalance(UpdateBalanceDTO updateBalanceDTO, Operation operation) {
        BankAccount bankAccount = getBankAccountById(updateBalanceDTO.accountId());

        BigDecimal newBalance = operation == Operation.ADD
                ? bankAccount.getBalance().add(updateBalanceDTO.value())
                : bankAccount.getBalance().subtract(updateBalanceDTO.value());

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient funds for the withdrawal, you have: " + bankAccount.getBalance() +
                    " you want to withdrawal: " + updateBalanceDTO.value());
        }

        bankAccount.setBalance(newBalance);

        bankAccountRepository.save(bankAccount);

        return modelMapper.map(bankAccount, BankAccountResponseDTO.class);
    }

    private enum Operation {
        ADD, SUBTRACT
    }

    private BankAccount getBankAccountById(UUID accountId) {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountIdNotFoundException("The bank account id: " + accountId + " was not found"));
    }

    private Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getDetails();
    }

    private void verifyUserIdMatch(Long userIdFromToken, Long userIdFromDto) {
        if (!userIdFromToken.equals(userIdFromDto)) {
            throw new UnauthorizedUserException("The user ID in the token does not match the requested user ID, userIdFromToken: "
                    + userIdFromToken + " userIdFromDto: " + userIdFromDto);
        }
    }
}
