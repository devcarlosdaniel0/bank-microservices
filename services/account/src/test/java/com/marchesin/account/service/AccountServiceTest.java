package com.marchesin.account.service;

import com.marchesin.account.domain.Account;
import com.marchesin.account.domain.CurrencyCode;
import com.marchesin.account.dto.*;
import com.marchesin.account.exception.*;
import com.marchesin.account.mapper.AccountMapper;
import com.marchesin.account.repository.AccountRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @Mock
    private AccountMapper mapper;

    @Mock
    private CurrencyConversionService conversionService;

    @InjectMocks
    private AccountService accountService;

    @Captor
    private ArgumentCaptor<Account> accountArgumentCaptor;

    private AuthenticatedUser authenticatedUser;
    private Account account;
    private AccountResponse accountResponse;
    private CreateAccountRequest createAccountRequest;
    private UpdateAccountRequest updateAccountRequest;

    @BeforeEach
    void setUp() {
        authenticatedUser = new AuthenticatedUser("user-123", "user@email.com", "user", true);

        createAccountRequest = new CreateAccountRequest("BRL");
        updateAccountRequest = new UpdateAccountRequest("USD");

        account = new Account("user-123", new CurrencyCode("BRL"));
        account.deposit(BigDecimal.valueOf(100));

        accountResponse = new AccountResponse(
                "acc-123",
                "user-123",
                "BRL",
                BigDecimal.valueOf(100),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    class createAccount {

        @Test
        @DisplayName("Should create account successfully when user is valid and has email verified")
        void shouldCreateAccountSuccessfullyWhenUserIsValidAndHasEmailVerified() {
            // Arrange
            when(repository.existsByUserId(authenticatedUser.id())).thenReturn(false);
            when(repository.save(any(Account.class))).thenReturn(account);
            when(mapper.fromAccount(account)).thenReturn(accountResponse);

            // Act
            accountService.createAccount(authenticatedUser, createAccountRequest);

            // Assert
            verify(repository).save(accountArgumentCaptor.capture());
            Account capturedAccount = accountArgumentCaptor.getValue();

            assertEquals("user-123", capturedAccount.getUserId());
            assertEquals("BRL", capturedAccount.getCurrencyCode());
            assertEquals(BigDecimal.ZERO, capturedAccount.getBalanceAmount());
        }

        @Test
        @DisplayName("Should initialize account with zero balance")
        void shouldInitializeAccountWithZeroBalance() {
            // Arrange
            when(repository.existsByUserId(authenticatedUser.id())).thenReturn(false);
            when(repository.save(any(Account.class))).thenReturn(account);
            when(mapper.fromAccount(account)).thenReturn(accountResponse);

            // Act
            accountService.createAccount(authenticatedUser, createAccountRequest);

            // Assert
            verify(repository).save(accountArgumentCaptor.capture());
            Account capturedAccount = accountArgumentCaptor.getValue();

            assertEquals(BigDecimal.ZERO, capturedAccount.getBalanceAmount());
        }

        @Test
        @DisplayName("Should throw exception when user already has an account")
        void shouldThrowExceptionWhenUserAlreadyHasAnAccount() {
            // Arrange
            when(repository.existsByUserId(authenticatedUser.id())).thenReturn(true);

            // Act & Assert
            assertThrows(UserAlreadyHasAccount.class, () ->
                    accountService.createAccount(authenticatedUser, createAccountRequest));

            verifyNoMoreInteractions(repository);
            verify(repository, never()).save(any(Account.class));
        }

        @Test
        @DisplayName("Should throw exception when user email is not verified")
        void shouldThrowExceptionWhenUserEmailIsNotVerified() {
            // Arrange
            AuthenticatedUser unverifiedUser = new AuthenticatedUser("user-123", "user@email.com", "user", false);
            when(repository.existsByUserId(unverifiedUser.id())).thenReturn(false);

            // Act & Assert
            assertThrows(UserEmailNotVerified.class, () ->
                    accountService.createAccount(unverifiedUser, createAccountRequest));

            verifyNoMoreInteractions(repository);
            verify(repository, never()).save(any(Account.class));
        }

        @Test
        @DisplayName("Should throw exception when currency code is invalid")
        void shouldThrowExceptionWhenCurrencyCodeIsInvalid() {
            // Arrange
            CreateAccountRequest invalidRequest = new CreateAccountRequest("INVALID");
            when(repository.existsByUserId(authenticatedUser.id())).thenReturn(false);

            // Act & Assert
            assertThrows(InvalidCurrencyCode.class, () ->
                    accountService.createAccount(authenticatedUser, invalidRequest));

            verifyNoMoreInteractions(repository);
            verify(repository, never()).save(any(Account.class));
        }

        @Test
        @DisplayName("Should normalize currency code to uppercase")
        void shouldNormalizeCurrencyCodeToUppercase() {
            // Arrange
            CreateAccountRequest lowercaseRequest = new CreateAccountRequest("usd");
            when(repository.existsByUserId(authenticatedUser.id())).thenReturn(false);
            when(repository.save(any(Account.class))).thenReturn(account);
            when(mapper.fromAccount(account)).thenReturn(accountResponse);

            // Act
            accountService.createAccount(authenticatedUser, lowercaseRequest);

            // Assert
            verify(repository).save(accountArgumentCaptor.capture());
            Account capturedAccount = accountArgumentCaptor.getValue();

            assertEquals("USD", capturedAccount.getCurrencyCode());
        }
    }

    @Nested
    class updateAccount {

        @Test
        @DisplayName("Should update account currency successfully and convert balance")
        void shouldUpdateAccountCurrencySuccessfullyAndConvertBalance() {
            // Arrange
            String userId = "user-123";
            BigDecimal convertedAmount = BigDecimal.valueOf(20);

            when(repository.findByUserId(userId)).thenReturn(Optional.of(account));
            when(conversionService.convert(any(), any(), eq(BigDecimal.valueOf(100)))).thenReturn(convertedAmount);
            when(mapper.fromAccount(account)).thenReturn(accountResponse);

            // Act
            accountService.updateAccount(userId, updateAccountRequest);

            // Assert
            assertEquals("USD", account.getCurrencyCode());
            assertEquals(convertedAmount, account.getBalanceAmount());

            verify(conversionService).convert(any(), any(), eq(BigDecimal.valueOf(100)));
        }

        @Test
        @DisplayName("Should throw exception and NOT call conversion service when same currency")
        void shouldThrowExceptionWhenUpdatingToSameCurrency() {
            // Arrange
            String userId = "user-123";
            UpdateAccountRequest sameRequest = new UpdateAccountRequest("BRL");

            when(repository.findByUserId(userId)).thenReturn(Optional.of(account));

            // Act & Assert
            assertThrows(SameCurrencyException.class, () ->
                    accountService.updateAccount(userId, sameRequest));

            verifyNoInteractions(conversionService);
        }

        @Test
        @DisplayName("Should throw exception when account not found")
        void shouldThrowExceptionWhenAccountNotFound() {
            // Arrange
            String userId = "non-existent-user";
            when(repository.findByUserId(userId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(AccountNotFound.class, () ->
                    accountService.updateAccount(userId, updateAccountRequest));

            verifyNoInteractions(conversionService, mapper);
        }

        @Test
        @DisplayName("Should normalize new currency code to uppercase")
        void shouldNormalizeNewCurrencyCodeToUppercase() {
            // Arrange
            String userId = "user-123";
            UpdateAccountRequest lowercaseRequest = new UpdateAccountRequest("eur");
            BigDecimal convertedAmount = BigDecimal.valueOf(15);

            when(repository.findByUserId(userId)).thenReturn(Optional.of(account));
            when(conversionService.convert(
                    any(CurrencyCode.class),
                    any(CurrencyCode.class),
                    any(BigDecimal.class)
            )).thenReturn(convertedAmount);
            when(mapper.fromAccount(account)).thenReturn(accountResponse);

            // Act
            accountService.updateAccount(userId, lowercaseRequest);

            // Assert
            assertEquals("EUR", account.getCurrencyCode());
            assertEquals(convertedAmount, account.getBalanceAmount());
        }
    }

    @Nested
    class findAll {

        @Test
        @DisplayName("Should return a Page of AccountResponse successfully")
        void shouldReturnPageOfAccountResponseSuccessfully() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Account> accountPage = new PageImpl<>(List.of(account));

            when(repository.findAll(pageable)).thenReturn(accountPage);
            when(mapper.fromAccount(account)).thenReturn(accountResponse);

            // Act
            Page<AccountResponse> result = accountService.findAll(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(accountResponse.currencyCode(), result.getContent().get(0).currencyCode());
            assertEquals(accountResponse.userId(), result.getContent().get(0).userId());

            verify(repository, times(1)).findAll(pageable);
            verify(mapper, times(1)).fromAccount(account);
        }

        @Test
        @DisplayName("Should return empty page when no accounts exist")
        void shouldReturnEmptyPageWhenNoAccountsExist() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Account> emptyPage = new PageImpl<>(List.of());

            when(repository.findAll(pageable)).thenReturn(emptyPage);

            // Act
            Page<AccountResponse> result = accountService.findAll(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());

            verify(repository, times(1)).findAll(pageable);
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Should apply pagination correctly")
        void shouldApplyPaginationCorrectly() {
            // Arrange
            Pageable pageable = PageRequest.of(2, 5);
            Page<Account> accountPage = new PageImpl<>(List.of(account), pageable, 50);

            when(repository.findAll(pageable)).thenReturn(accountPage);
            when(mapper.fromAccount(account)).thenReturn(accountResponse);

            // Act
            Page<AccountResponse> result = accountService.findAll(pageable);

            // Assert
            assertEquals(50, result.getTotalElements());
            assertEquals(2, result.getNumber());
            assertEquals(5, result.getSize());

            verify(repository, times(1)).findAll(pageable);
        }
    }

    @Nested
    class deleteAccount {

        @Test
        @DisplayName("Should delete account successfully")
        void shouldDeleteAccountSuccessfully() {
            // Arrange
            String userId = "user-123";

            when(repository.findByUserId(userId)).thenReturn(Optional.of(account));

            // Act
            accountService.deleteAccount(userId);

            // Assert
            verify(repository, times(1)).findByUserId(userId);
            verify(repository, times(1)).delete(account);
        }

        @Test
        @DisplayName("Should throw exception when account not found")
        void shouldThrowExceptionWhenAccountNotFound() {
            // Arrange
            String userId = "non-existent-user";

            when(repository.findByUserId(userId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(AccountNotFound.class, () ->
                    accountService.deleteAccount(userId));

            verify(repository, times(1)).findByUserId(userId);
            verifyNoMoreInteractions(repository);
        }
    }

    @Nested
    class getBalance {

        @Test
        @DisplayName("Should return account balance successfully")
        void shouldReturnAccountBalanceSuccessfully() {
            // Arrange
            String userId = "user-123";
            BigDecimal expectedBalance = BigDecimal.valueOf(100);

            when(repository.findByUserId(userId)).thenReturn(Optional.of(account));

            // Act
            BalanceResponse result = accountService.getBalance(userId);

            // Assert
            assertNotNull(result);
            assertEquals(expectedBalance, result.amount());
            assertEquals("BRL", result.currencyCode());
        }

        @Test
        @DisplayName("Should return zero balance for new account")
        void shouldReturnZeroBalanceForNewAccount() {
            // Arrange
            String userId = "user-123";
            Account newAccount = new Account(userId, new CurrencyCode("EUR"));

            when(repository.findByUserId(userId)).thenReturn(Optional.of(newAccount));

            // Act
            BalanceResponse result = accountService.getBalance(userId);

            // Assert
            assertNotNull(result);
            assertEquals(BigDecimal.ZERO, result.amount());
            assertEquals("EUR", result.currencyCode());
        }

        @Test
        @DisplayName("Should throw exception when account not found")
        void shouldThrowExceptionWhenAccountNotFound() {
            // Arrange
            String userId = "non-existent-user";

            when(repository.findByUserId(userId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(AccountNotFound.class, () ->
                    accountService.getBalance(userId));
        }
    }

}