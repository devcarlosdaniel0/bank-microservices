package com.project.bank.service;

import com.project.bank.clients.CurrencyConverterClient;
import com.project.bank.dto.*;
import com.project.bank.exception.*;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Mock
    private Authentication authentication;

    @Mock
    private CurrencyConverterClient currencyConverterClient;

    @InjectMocks
    private BankAccountService bankAccountService;

    @Captor
    private ArgumentCaptor<BankAccount> bankAccountArgumentCaptor;

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

        lenient().when(modelMapper.map(any(BankAccount.class), eq(BankAccountResponseDTO.class)))
                .thenAnswer(invocation -> {
                    BankAccount source = invocation.getArgument(0);
                    return new BankAccountResponseDTO(
                            source.getId(),
                            source.getUser().getId(),
                            source.getAccountEmail(),
                            source.getAccountName(),
                            source.getBalance(),
                            source.getCurrency()
                    );
                });

        lenient().when(authentication.getDetails()).thenReturn(1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    class createBankAccount {

        @Test
        @DisplayName("Should create bank account successfully when user exists and has no bank account and is confirmed")
        void shouldCreateBankAccountSuccessfullyWhenUserExistsAndHasNoBankAccountAndIsConfirmed() {
            // Arrange
            when(userEntityRepository.findById(userIdFromToken)).thenReturn(Optional.of(user));
            when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

            // Act
            BankAccountResponseDTO result = bankAccountService.createBankAccount(createBankAccountDTO);
            result.setId(bankAccount.getId());

            // Assert
            verify(bankAccountRepository).save(bankAccountArgumentCaptor.capture());
            var bankAccountCaptured = bankAccountArgumentCaptor.getValue();

            assertNotNull(result);
            assertEquals(bankAccount.getId(), result.getId());
            assertEquals(bankAccount.getBalance(), result.getBalance());
            assertEquals(bankAccount.getUser().getId(), result.getUserId());
            assertEquals(bankAccount.getUser().getUsername(), result.getAccountName());
            assertEquals(bankAccount.getCurrency(), result.getCurrency());

            assertEquals(user, bankAccountCaptured.getUser());
            assertEquals(BigDecimal.ZERO, bankAccountCaptured.getBalance());

            verify(userEntityRepository, times(1)).findById(userIdFromToken);
            verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
        }

        @Test
        @DisplayName("Should throw exception when user id from token is not found")
        void shouldThrowExceptionWhenUserIdFromTokenIsNotFound() {
            // Arrange
            when(userEntityRepository.findById(userIdFromToken)).thenReturn(Optional.empty());

            // Act & Assert
            UserIdNotFoundException exception = assertThrows(UserIdNotFoundException.class,
                    () -> bankAccountService.createBankAccount(createBankAccountDTO));

            assertEquals("User ID: " + userIdFromToken + " not found", exception.getMessage());
            verify(userEntityRepository, times(1)).findById(userIdFromToken);
            verifyNoInteractions(bankAccountRepository);
        }

        @Test
        @DisplayName("Should throw exception when user already has a bank account")
        void ShouldThrowExceptionWhenUserAlreadyHasABankAccount() {
            // Arrange
            user.setBankAccount(bankAccount);

            when(userEntityRepository.findById(userIdFromToken)).thenReturn(Optional.of(user));

            // Act & Assert
            UserAlreadyHasBankAccountException exception = assertThrows(UserAlreadyHasBankAccountException.class,
                    () -> bankAccountService.createBankAccount(createBankAccountDTO));

            assertEquals("User already has a bank account", exception.getMessage());
            verify(userEntityRepository, times(1)).findById(userIdFromToken);
            verifyNoInteractions(bankAccountRepository);
        }

        @Test
        @DisplayName("Should throw exception when user are not confirmed")
        void ShouldThrowExceptionWhenUserAreNotConfirmed() {
            // Arrange
            user.setConfirmed(false);

            when(userEntityRepository.findById(userIdFromToken)).thenReturn(Optional.of(user));

            // Act & Assert
            UnconfirmedUserException e = assertThrows(UnconfirmedUserException.class,
                    () -> bankAccountService.createBankAccount(createBankAccountDTO));

            assertEquals("Your user are not confirmed! Please confirm your account", e.getMessage());
            verify(userEntityRepository, times(1)).findById(userIdFromToken);
            verifyNoInteractions(bankAccountRepository);
        }

        @Test
        @DisplayName("Should throw exception when currency code is invalid")
        void ShouldThrowExceptionWhenCurrencyCodeIsInvalid() {
            // Arrange
            when(userEntityRepository.findById(userIdFromToken)).thenReturn(Optional.of(user));

            CreateBankAccountDTO invalidDTO = new CreateBankAccountDTO("ABC");

            // Act & Assert
            InvalidCurrencyCodeException e = assertThrows(InvalidCurrencyCodeException.class,
                    () -> bankAccountService.createBankAccount(invalidDTO));

            assertEquals("Example: BRL, USD, CAD, AUD", e.getMessage());
            verifyNoInteractions(bankAccountRepository);
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

    @Nested
    class addBalance {
        @Test
        @DisplayName("Should add balance when successfully")
        void shouldAddBalanceWhenSuccessfully() {
            // Arrange
            var updateBalanceDTO = new UpdateBalanceDTO(BigDecimal.valueOf(100));

            user.setBankAccount(bankAccount);

            when(userEntityRepository.findById(userIdFromToken))
                    .thenReturn(Optional.of(user));
            when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

            // Act
            BankAccountResponseDTO response = bankAccountService.addBalance(updateBalanceDTO);

            // Assert
            verify(bankAccountRepository).save(bankAccountArgumentCaptor.capture());
            BankAccount capturedBankAccount = bankAccountArgumentCaptor.getValue();

            assertNotNull(response);
            assertEquals(BigDecimal.valueOf(100), capturedBankAccount.getBalance());
            assertEquals(bankAccount.getId(), response.getId());
            assertEquals(BigDecimal.valueOf(100), response.getBalance());
        }
    }

    @Nested
    class withdrawalBalance {
        @Test
        @DisplayName("Should withdrawal balance successfully when the value is not bigger than the actual balance")
        void shouldWithdrawalBalanceSuccessfullyWhenTheValueIsNotBiggerThenTheActualBalance() {
            // Arrange
            var updateBalanceDTO = new UpdateBalanceDTO(BigDecimal.valueOf(100));
            bankAccount.setBalance(BigDecimal.valueOf(100));

            user.setBankAccount(bankAccount);

            when(userEntityRepository.findById(userIdFromToken))
                    .thenReturn(Optional.of(user));
            when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

            // Act
            BankAccountResponseDTO response = bankAccountService.withdrawalBalance(updateBalanceDTO);

            // Assert
            verify(bankAccountRepository).save(bankAccountArgumentCaptor.capture());
            BankAccount capturedBankAccount = bankAccountArgumentCaptor.getValue();

            assertNotNull(response);
            assertEquals(BigDecimal.valueOf(0), capturedBankAccount.getBalance());
            assertEquals(bankAccount.getId(), response.getId());
            assertEquals(BigDecimal.valueOf(0), response.getBalance());
        }

        @Test
        @DisplayName("Should throw exception when the value it's bigger than the actual balance")
        void shouldThrowExceptionWhenTheValueItsBiggerThanTheActualBalance() {
            // Arrange
            var updateBalanceDTO = new UpdateBalanceDTO(BigDecimal.valueOf(100));

            user.setBankAccount(bankAccount);

            when(userEntityRepository.findById(userIdFromToken))
                    .thenReturn(Optional.of(user));

            // Act & Assert
            InsufficientFundsException e = assertThrows(InsufficientFundsException.class, () ->
                    bankAccountService.withdrawalBalance(updateBalanceDTO));

            assertEquals(String.format(
                    "Insufficient funds. Current balance is %s, attempted withdrawal: %s",
                    bankAccount.getBalance(), updateBalanceDTO.value()
            ), e.getMessage());
            verify(bankAccountRepository, never()).save(any(BankAccount.class));
            verifyNoMoreInteractions(bankAccountRepository);
        }
    }

    @Nested
    class updateBalance {
        @Test
        @DisplayName("Should throw exception when user does not have a bank account")
        void shouldThrowExceptionWhenUserDoesNotHaveBankAccount() {
            // Arrange
            user.setBankAccount(null);
            var updateBalanceDTO = new UpdateBalanceDTO(BigDecimal.valueOf(50));

            when(userEntityRepository.findById(userIdFromToken))
                    .thenReturn(Optional.of(user));

            // Act & Assert
            BankAccountNotFoundException e = assertThrows(BankAccountNotFoundException.class, () ->
                    bankAccountService.addBalance(updateBalanceDTO));

            assertEquals("User does not have a bank account", e.getMessage());
            verify(bankAccountRepository, never()).save(any(BankAccount.class));
        }
    }

    @Nested
    class findBankAccountIdByAccountEmail {
        @Test
        @DisplayName("Should find bank account ID by account email when bank account exists")
        void shouldFindBankAccountIdByAccountEmailWhenBankAccountExists() {
            // Arrange
            when(bankAccountRepository.findByAccountEmail(bankAccount.getAccountEmail())).thenReturn(Optional.of(bankAccount));

            // Act
            BankAccountFoundDTO bankAccountFoundDTO = bankAccountService.findBankAccountIdByAccountEmail(bankAccount.getAccountEmail());

            // Assert
            assertNotNull(bankAccountFoundDTO);
            assertEquals(bankAccount.getId(), bankAccountFoundDTO.accountId());
        }

        @Test
        @DisplayName("Should throw exception when bank account not exists")
        void shouldThrowExceptionWhenBankAccountNotExists() {
            // Arrange
            var accountEmail = "sadhuaiu@gmail.com";
            when(bankAccountRepository.findByAccountEmail(accountEmail)).thenReturn(Optional.empty());

            // Act & Assert
            BankAccountIdNotFoundException e = assertThrows(BankAccountIdNotFoundException.class,
                    () -> bankAccountService.findBankAccountIdByAccountEmail(accountEmail));

            assertEquals("The bank account email: '" + accountEmail + "' was not found", e.getMessage());
        }
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
                    .response(String.format("Your current balance is: %s %s and you transferred %s %s to account ID %s (%s | %s)",
                            sender.getBalance().subtract(transferValue),
                            sender.getCurrency().getCurrencyCode(),
                            transferDTO.value(),
                            receiverBankAccount.getCurrency().getCurrencyCode(),
                            receiverBankAccount.getId(),
                            receiverBankAccount.getAccountName(),
                            receiverBankAccount.getAccountEmail())).build();

            // Act
            TransferResponseDTO response = bankAccountService.transfer(transferDTO);

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
                    .response(String.format("Your current balance is: %s %s and you transferred %s %s to account ID %s (%s | %s). The receiver got %s %s after conversion.",
                            sender.getBalance().subtract(transferValue),
                            senderCurrency,
                            transferDTO.value(),
                            senderCurrency,
                            receiverBankAccount.getId(),
                            receiverBankAccount.getAccountName(),
                            receiverBankAccount.getAccountEmail(),
                            convertedAmount,
                            receiverCurrency)).build();

            // Act
            TransferResponseDTO response = bankAccountService.transfer(transferDTO);

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
            TransferNotAllowedException e = assertThrows(TransferNotAllowedException.class, () -> bankAccountService
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
            InsufficientFundsException e = assertThrows(InsufficientFundsException.class, () -> bankAccountService.transfer(transferDTO));

            assertEquals(String.format(
                    "Insufficient funds. Current balance is %s, attempted transfer: %s",
                    sender.getBalance(), transferDTO.value()
            ), e.getMessage());
            assertEquals(BigDecimal.valueOf(5), sender.getBalance());
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
                    () -> bankAccountService.transfer(transferDTO));

            assertEquals("You cant transfer to your own bank account", e.getMessage());
        }
    }
}