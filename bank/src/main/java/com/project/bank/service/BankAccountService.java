package com.project.bank.service;

import com.project.bank.dto.CreateBankAccountDTO;
import com.project.core.domain.BankAccount;
import com.project.core.domain.UserEntity;
import com.project.core.repository.BankAccountRepository;
import com.project.core.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserEntityRepository userEntityRepository;

    @Transactional
    public BankAccount createBankAccount(CreateBankAccountDTO dto) {
        UserEntity user = userEntityRepository.findById(dto.userID())
                .orElseThrow(() -> new RuntimeException("User ID not found"));

        if (user.getBankAccount() != null) {
            throw new RuntimeException("User already has a bank account");
        }

        BankAccount bankAccount = BankAccount.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .build();

        return bankAccountRepository.save(bankAccount);
    }
}
