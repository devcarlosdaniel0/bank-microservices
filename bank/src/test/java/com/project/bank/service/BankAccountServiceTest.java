package com.project.bank.service;

import com.project.bank.dto.CreateBankAccountDTO;
import com.project.core.domain.BankAccount;
import com.project.core.domain.UserEntity;
import com.project.core.domain.UserRole;
import com.project.core.repository.BankAccountRepository;
import com.project.core.repository.UserEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserEntityRepository userEntityRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    @Captor
    private ArgumentCaptor<BankAccount> bankAccountArgumentCaptor;

    @Nested
    class createBankAccount {

        @Test
        @DisplayName("Should create bank account when user exists and has no bank account")
        void shouldCreateBankAccountWhenUserExistsAndHasNoBankAccount() {
            // Arrange
            var dto = new CreateBankAccountDTO(1L);
            var user = new UserEntity(1L, "carlos", "aodfiso", UserRole.USER, null);
            var expectedBankAccount = BankAccount.builder()
                    .id(1L)
                    .balance(BigDecimal.ZERO)
                    .user(user)
                    .build();

            when(userEntityRepository.findById(dto.userID())).thenReturn(Optional.of(user));
            when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(expectedBankAccount);

            // Act
            BankAccount result = bankAccountService.createBankAccount(dto);

            // Assert
            verify(bankAccountRepository).save(bankAccountArgumentCaptor.capture());
            var bankAccountCaptured = bankAccountArgumentCaptor.getValue();

            assertNotNull(result);
            assertEquals(expectedBankAccount.getId(), result.getId());
            assertEquals(expectedBankAccount.getUser(), result.getUser());
            assertEquals(expectedBankAccount.getBalance(), result.getBalance());

            assertEquals(user, bankAccountCaptured.getUser());
            assertEquals(BigDecimal.ZERO, bankAccountCaptured.getBalance());

            verify(userEntityRepository, times(1)).findById(dto.userID());
            verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
        }

        @Test
        @DisplayName("Should throw exception when user id is not found")
        void shouldThrowExceptionWhenUserIdIsNotFound() {
            // Arrange
            var dto = new CreateBankAccountDTO(1L);

            when(userEntityRepository.findById(dto.userID())).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> bankAccountService.createBankAccount(dto));

            assertEquals("User ID not found", exception.getMessage());
            verify(userEntityRepository, times(1)).findById(dto.userID());
            verifyNoInteractions(bankAccountRepository);
        }

        @Test
        @DisplayName("Should throw exception when user already has a bank account")
        void ShouldThrowExceptionWhenUserAlreadyHasABankAccount() {
            // Arrange
            var dto = new CreateBankAccountDTO(1L);
            var user = new UserEntity(1L, "carlos", "aodfiso", UserRole.USER, null);
            var existingBankAccount = BankAccount.builder()
                    .id(1L)
                    .balance(BigDecimal.ZERO)
                    .user(user)
                    .build();

            user.setBankAccount(existingBankAccount);

            when(userEntityRepository.findById(dto.userID())).thenReturn(Optional.of(user));


            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> bankAccountService.createBankAccount(dto));

            assertEquals("User already has a bank account", exception.getMessage());
            verify(userEntityRepository, times(1)).findById(dto.userID());
            verifyNoInteractions(bankAccountRepository);
        }

        @Test
        @DisplayName("Should throw exception when DTO is null")
        void shouldThrowExceptionWhenDTOIsNull() {
            // Act & Assert
            assertThrows(NullPointerException.class, () -> bankAccountService.createBankAccount(null));
        }
    }
}