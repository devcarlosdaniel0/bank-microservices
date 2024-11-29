package com.project.bank.service;

import com.project.bank.dto.BankAccountResponseDTO;
import com.project.bank.dto.CreateBankAccountDTO;
import com.project.bank.exception.UserAlreadyHasBankAccountException;
import com.project.bank.exception.UserIdNotFoundException;
import com.project.core.domain.BankAccount;
import com.project.core.domain.UserEntity;
import com.project.core.repository.BankAccountRepository;
import com.project.core.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserEntityRepository userEntityRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public BankAccount createBankAccount(CreateBankAccountDTO dto) {
        UserEntity user = userEntityRepository.findById(dto.userID())
                .orElseThrow(() -> new UserIdNotFoundException("User ID not found"));

        if (user.getBankAccount() != null) {
            throw new UserAlreadyHasBankAccountException("User already has a bank account");
        }

        BankAccount bankAccount = BankAccount.builder()
                .accountName(user.getUsername())
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();

        return bankAccountRepository.save(bankAccount);
    }

    public List<BankAccountResponseDTO> findAll() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();

        return bankAccounts.stream()
                .map(account -> modelMapper.map(account, BankAccountResponseDTO.class))
                .toList();
    }
}
