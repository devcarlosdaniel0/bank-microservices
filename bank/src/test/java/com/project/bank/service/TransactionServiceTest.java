package com.project.bank.service;

import com.project.auth.security.exception.EmailNotFoundException;
import com.project.bank.clients.CurrencyConverterClient;
import com.project.bank.dto.CreateBankAccountDTO;
import com.project.bank.dto.CurrencyResponse;
import com.project.bank.dto.TransferDTO;
import com.project.bank.dto.TransferResponseDTO;
import com.project.bank.exception.InsufficientFundsException;
import com.project.bank.exception.TransferNotAllowedException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    CurrencyConverterClient currencyConverterClient;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TransactionService transactionService;

    private Long userIdFromToken;
    private UserEntity user;
    private BankAccount bankAccount;
    private UserEntity receiver;
    private BankAccount receiverBankAccount;
    private CreateBankAccountDTO createBankAccountDTO;

    @BeforeEach
    void setUp() {
        userIdFromToken = 1L;

        createBankAccountDTO = new CreateBankAccountDTO("BRL");

        user = new UserEntity(1L, "carlos@gmail.com", "carlos", "123", UserRole.USER, null, null, null, true);
        bankAccount = BankAccount.builder()
                .id(UUID.randomUUID())
                .user(user)
                .accountEmail(user.getEmail())
                .accountName(user.getUsername())
                .balance(BigDecimal.ZERO)
                .currency(Currency.getInstance(createBankAccountDTO.currencyCode()))
                .build();

        receiver = new UserEntity(2L, "isaque@gmail.com", "isaque", "123", UserRole.USER, null, null, null, true);
        receiverBankAccount = BankAccount.builder()
                .id(UUID.randomUUID())
                .accountEmail(receiver.getEmail())
                .accountName(receiver.getUsername())
                .balance(BigDecimal.ZERO)
                .currency(Currency.getInstance(createBankAccountDTO.currencyCode()))
                .user(receiver)
                .build();

        lenient().when(authentication.getDetails()).thenReturn(1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    class transfer {
        @Test
        @DisplayName("Should transfer successfully when the value its greater than zero and account has enough balance and has same currency")
        void shouldTransferSuccessfullyWhenTheValueItsGreaterThanZeroAndAccountHasEnoughBalanceAndHasSameCurrency() {
            // Arrange
            when(userEntityRepository.findById(userIdFromToken)).thenReturn(Optional.of(user));
            user.setBankAccount(bankAccount);

            BankAccount sender = user.getBankAccount();
            sender.setBalance(BigDecimal.valueOf(10));

            receiver.setBankAccount(receiverBankAccount);

            var transferValue = BigDecimal.valueOf(10);
            var transferDTO = new TransferDTO(receiverBankAccount.getAccountEmail(), transferValue);

            when(bankAccountRepository.findByAccountEmail(transferDTO.receiverAccountEmail()))
                    .thenReturn(Optional.of(receiverBankAccount));

            var expectedResponse = TransferResponseDTO.builder()
                    .senderCurrentBalance(sender.getBalance().subtract(transferValue))
                    .senderCurrencyCode(sender.getCurrency().getCurrencyCode())
                    .transferredValue(transferDTO.value())
                    .receiverCurrencyCode(receiverBankAccount.getCurrency().getCurrencyCode())
                    .receiverName(receiverBankAccount.getAccountName())
                    .receiverEmail(receiverBankAccount.getAccountEmail()).build();

            // Act
            TransferResponseDTO response = transactionService.transfer(transferDTO);

            // Assert
            assertNotNull(response);
            assertEquals(expectedResponse, response);
            assertEquals(BigDecimal.valueOf(0), sender.getBalance());
            assertEquals(BigDecimal.valueOf(10), receiverBankAccount.getBalance());
        }

        @Test
        @DisplayName("Should transfer successfully when the value its greater than zero and account has enough balance and has different currency")
        void shouldTransferSuccessfullyWhenTheValueItsGreaterThanZeroAndAccountHasEnoughBalanceAndHasDifferentCurrency() {
            // Arrange
            when(userEntityRepository.findById(userIdFromToken)).thenReturn(Optional.of(user));
            user.setBankAccount(bankAccount);

            BankAccount sender = user.getBankAccount();
            sender.setBalance(BigDecimal.valueOf(100));

            receiver.setBankAccount(receiverBankAccount);
            receiverBankAccount.setCurrency(Currency.getInstance("USD"));

            var transferValue = BigDecimal.valueOf(50);
            var transferDTO = new TransferDTO(receiverBankAccount.getAccountEmail(), transferValue);

            var senderCurrency = sender.getCurrency().getCurrencyCode();
            var receiverCurrency = receiverBankAccount.getCurrency().getCurrencyCode();

            CurrencyResponse expectedResponseClient = CurrencyResponse.builder()
                    .symbols(String.format("%s_%s", senderCurrency, receiverCurrency))
                    .exchangeRate(BigDecimal.valueOf(0.16))
                    .amount(transferValue)
                    .convertedAmount(BigDecimal.valueOf(8.25))
                    .timestamp(LocalDateTime.now())
                    .build();

            BigDecimal convertedAmount = expectedResponseClient.convertedAmount();

            when(bankAccountRepository.findByAccountEmail(transferDTO.receiverAccountEmail()))
                    .thenReturn(Optional.of(receiverBankAccount));

            when(currencyConverterClient.convertCurrencies(transferValue, String.format("%s_%s",
                    senderCurrency, receiverCurrency))).thenReturn(expectedResponseClient);

            var expectedResponse = TransferResponseDTO.builder()
                    .senderCurrentBalance(sender.getBalance().subtract(transferValue))
                    .senderCurrencyCode(senderCurrency)
                    .transferredValue(transferDTO.value())
                    .receiverName(receiverBankAccount.getAccountName())
                    .receiverEmail(receiverBankAccount.getAccountEmail())
                    .convertedAmount(convertedAmount)
                    .receiverCurrencyCode(receiverCurrency).build();

            // Act
            TransferResponseDTO response = transactionService.transfer(transferDTO);

            // Assert
            assertNotNull(response);
            assertEquals(expectedResponse, response);
            assertEquals(BigDecimal.valueOf(50), sender.getBalance());
            assertEquals(BigDecimal.valueOf(8.25), receiverBankAccount.getBalance());
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "-1", "-10.5"})
        @DisplayName("Should throw exception when transfer value its lower or equal than zero")
        void shouldThrowExceptionWhenTransferValueItsLowerOrEqualThanZero(String value) {
            // Arrange
            var transferDTO = new TransferDTO(null, new BigDecimal(value));

            // Act & Assert
            TransferNotAllowedException e = assertThrows(TransferNotAllowedException.class, () -> transactionService
                    .transfer(transferDTO));

            assertEquals("Transfer value must be greater than zero.", e.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when bank account has insufficient founds")
        void shouldThrowExceptionWhenBankAccountHasInsufficientFounds() {
            // Arrange
            when(userEntityRepository.findById(userIdFromToken)).thenReturn(Optional.of(user));
            user.setBankAccount(bankAccount);

            BankAccount sender = user.getBankAccount();
            sender.setBalance(BigDecimal.valueOf(5));

            receiver.setBankAccount(receiverBankAccount);

            var transferValue = BigDecimal.valueOf(10);
            var transferDTO = new TransferDTO(receiverBankAccount.getAccountEmail(), transferValue);

            when(bankAccountRepository.findByAccountEmail(transferDTO.receiverAccountEmail())).thenReturn(Optional.of(receiverBankAccount));

            // Act & Assert
            InsufficientFundsException e = assertThrows(InsufficientFundsException.class, () -> transactionService.transfer(transferDTO));

            assertEquals(String.format(
                    "Insufficient funds. Current balance is %s, attempted transfer: %s",
                    sender.getBalance(), transferDTO.value()
            ), e.getMessage());
            assertEquals(BigDecimal.valueOf(5), sender.getBalance());
        }

        @Test
        @DisplayName("Should throw exception when email is not found")
        void shouldThrowExceptionWhenEmailIsNotFound() {
            when(userEntityRepository.findById(userIdFromToken)).thenReturn(Optional.of(user));
            user.setBankAccount(bankAccount);

            var transferValue = BigDecimal.valueOf(10);
            var transferDTO = new TransferDTO("email@gmail.com", transferValue);

            when(bankAccountRepository.findByAccountEmail(transferDTO.receiverAccountEmail())).thenReturn(Optional.empty());

            EmailNotFoundException e = assertThrows(EmailNotFoundException.class,
                    () -> transactionService.transfer(transferDTO));

            assertEquals(String.format("Email: %s was not found", transferDTO.receiverAccountEmail()), e.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when trying to transfer to own account")
        void shouldThrowExceptionWhenTryingToTransferToOwnAccount() {
            // Arrange
            when(userEntityRepository.findById(userIdFromToken)).thenReturn(Optional.of(user));
            user.setBankAccount(bankAccount);

            BankAccount sender = user.getBankAccount();
            sender.setBalance(BigDecimal.valueOf(10));

            var transferValue = BigDecimal.valueOf(5);
            var transferDTO = new TransferDTO(sender.getAccountEmail(), transferValue);

            when(bankAccountRepository.findByAccountEmail(transferDTO.receiverAccountEmail())).thenReturn(Optional.of(sender));

            // Act & Assert
            TransferNotAllowedException e = assertThrows(TransferNotAllowedException.class,
                    () -> transactionService.transfer(transferDTO));

            assertEquals("You cant transfer to your own bank account", e.getMessage());
        }
    }
}