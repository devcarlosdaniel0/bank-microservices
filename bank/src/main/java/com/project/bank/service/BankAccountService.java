package com.project.bank.service;

import com.project.bank.dto.BankAccountResponseDTO;
import com.project.bank.dto.CreateBankAccountDTO;
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
import java.util.stream.Collectors;

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

    public List<BankAccountResponseDTO> findAll() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();

        ModelMapper modelMapper = new ModelMapper();

        List<BankAccountResponseDTO> dto = bankAccounts.stream()
                .map(account -> modelMapper.map(account, BankAccountResponseDTO.class))
                .collect(Collectors.toList());

        return dto;
    }
}
