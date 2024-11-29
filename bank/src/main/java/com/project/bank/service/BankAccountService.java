package com.project.bank.service;

import com.project.bank.dto.AddBalanceDTO;
import com.project.bank.dto.BankAccountResponseDTO;
import com.project.bank.dto.CreateBankAccountDTO;
import com.project.bank.exception.BankAccountIdNotFoundException;
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
    public BankAccountResponseDTO createBankAccount(CreateBankAccountDTO dto) {
        UserEntity user = userEntityRepository.findById(dto.userId())
                .orElseThrow(() -> new UserIdNotFoundException("User ID not found"));

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
    public BankAccountResponseDTO addBalance(AddBalanceDTO addBalanceDTO) {
        BankAccount bankAccount = bankAccountRepository.findById(addBalanceDTO.accountId())
                .orElseThrow(() -> new BankAccountIdNotFoundException("The bank account id was not found"));

        bankAccount.setBalance(bankAccount.getBalance().add(addBalanceDTO.value()));

        bankAccountRepository.save(bankAccount);

        return modelMapper.map(bankAccount, BankAccountResponseDTO.class);
    }

    @Transactional
    public BankAccountResponseDTO withdrawalBalance(AddBalanceDTO addBalanceDTO) {
        BankAccount bankAccount = bankAccountRepository.findById(addBalanceDTO.accountId())
                .orElseThrow(() -> new BankAccountIdNotFoundException("The bank account id was not found"));

        bankAccount.setBalance(bankAccount.getBalance().subtract(addBalanceDTO.value()));

        bankAccountRepository.save(bankAccount);

        return modelMapper.map(bankAccount, BankAccountResponseDTO.class);
    }

    public List<BankAccountResponseDTO> findAll() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();

        return bankAccounts.stream()
                .map(account -> modelMapper.map(account, BankAccountResponseDTO.class))
                .toList();
    }
}
