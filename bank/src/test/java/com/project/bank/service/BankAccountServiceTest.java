package com.project.bank.service;

import com.project.bank.dto.BankAccountResponseDTO;
import com.project.bank.dto.CreateBankAccountDTO;
import com.project.bank.exception.UserAlreadyHasBankAccountException;
import com.project.bank.exception.UserIdNotFoundException;
import com.project.core.domain.BankAccount;
import com.project.core.domain.UserEntity;
import com.project.core.domain.UserRole;
import com.project.core.repository.BankAccountRepository;
import com.project.core.repository.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserEntityRepository userEntityRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    @Captor
    private ArgumentCaptor<BankAccount> bankAccountArgumentCaptor;

    private CreateBankAccountDTO dto;
    private UserEntity user;
    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        dto = new CreateBankAccountDTO(1L);
        user = new UserEntity(1L, "carlos", "123", UserRole.USER, null);
        bankAccount = BankAccount.builder()
                .accountName(user.getUsername())
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();

        lenient().when(modelMapper.map(any(BankAccount.class), eq(BankAccountResponseDTO.class)))
                .thenAnswer(invocation -> {
                    BankAccount source = invocation.getArgument(0);
                    return new BankAccountResponseDTO(
                                                source.getId(),
                            source.getBalance(),
                            source.getUser().getId(),
                            source.getAccountName()
                                        );
                });
    }

    @Nested
    class createBankAccount {

        @Test
        @DisplayName("Should create bank account when user exists and has no bank account")
        void shouldCreateBankAccountWhenUserExistsAndHasNoBankAccount() {
            // Arrange
            when(userEntityRepository.findById(dto.userId())).thenReturn(Optional.of(user));
            when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

            // Act
            BankAccountResponseDTO result = bankAccountService.createBankAccount(dto);

            // Assert
            verify(bankAccountRepository).save(bankAccountArgumentCaptor.capture());
            var bankAccountCaptured = bankAccountArgumentCaptor.getValue();

            assertNotNull(result);
            assertEquals(bankAccount.getId(), result.getId());
            assertEquals(bankAccount.getBalance(), result.getBalance());
            assertEquals(bankAccount.getUser().getId(), result.getUserId());
            assertEquals(bankAccount.getUser().getUsername(), result.getAccountName());

            assertEquals(user, bankAccountCaptured.getUser());
            assertEquals(BigDecimal.ZERO, bankAccountCaptured.getBalance());

            verify(userEntityRepository, times(1)).findById(dto.userId());
            verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
        }

        @Test
        @DisplayName("Should throw exception when user id is not found")
        void shouldThrowExceptionWhenUserIdIsNotFound() {
            // Arrange
            when(userEntityRepository.findById(dto.userId())).thenReturn(Optional.empty());

            // Act & Assert
            UserIdNotFoundException exception = assertThrows(UserIdNotFoundException.class, () -> bankAccountService.createBankAccount(dto));

            assertEquals("User ID not found", exception.getMessage());
            verify(userEntityRepository, times(1)).findById(dto.userId());
            verifyNoInteractions(bankAccountRepository);
        }

        @Test
        @DisplayName("Should throw exception when user already has a bank account")
        void ShouldThrowExceptionWhenUserAlreadyHasABankAccount() {
            // Arrange
            user.setBankAccount(bankAccount);

            when(userEntityRepository.findById(dto.userId())).thenReturn(Optional.of(user));

            // Act & Assert
            UserAlreadyHasBankAccountException exception = assertThrows(UserAlreadyHasBankAccountException.class,
                    () -> bankAccountService.createBankAccount(dto));

            assertEquals("User already has a bank account", exception.getMessage());
            verify(userEntityRepository, times(1)).findById(dto.userId());
            verifyNoInteractions(bankAccountRepository);
        }

        @Test
        @DisplayName("Should throw exception when DTO is null")
        void shouldThrowExceptionWhenDTOIsNull() {
            // Act & Assert
            assertThrows(NullPointerException.class, () -> bankAccountService.createBankAccount(null));
        }
    }

    @Nested
    class findAll {
        @Test
        @DisplayName("Should return a list of BankResponseDTO when successfully")
        void shouldReturnListOfBankResponseDTOWhenSuccessfully() {
            // Arrange
            when(bankAccountRepository.findAll()).thenReturn(List.of(bankAccount));

            // Act
            List<BankAccountResponseDTO> result = bankAccountService.findAll();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(bankAccount.getBalance(), result.get(0).getBalance());
            assertEquals(user.getId(), result.get(0).getUserId());
            assertEquals(user.getUsername(), result.get(0).getAccountName());
        }
    }
}