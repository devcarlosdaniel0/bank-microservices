package com.project.bank.handler;

import com.project.auth.security.exception.EmailNotFoundException;
import com.project.bank.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class BankExceptionHandler {

    @ExceptionHandler(UserAlreadyHasBankAccountException.class)
    public ResponseEntity<ProblemDetail> handlerUserAlreadyHasBankAccountException(
            UserAlreadyHasBankAccountException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problemDetail.setTitle("This user already has a bank account registered!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerUserIdNotFoundException(
            UserIdNotFoundException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("The user id was not found!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(BankAccountIdNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerBankAccountIdNotFoundException(
            BankAccountIdNotFoundException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("The bank account id was not found!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ProblemDetail> handlerInsufficientFundsException(
            InsufficientFundsException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("The value you want to withdrawal or transfer it's bigger than you have!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(TransferNotAllowedException.class)
    public ResponseEntity<ProblemDetail> handlerTransferNotAllowedException(
            TransferNotAllowedException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
        problemDetail.setTitle("That transfer can not be allowed!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<ProblemDetail> handlerUnauthorizedUserException(
            UnauthorizedUserException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
        problemDetail.setTitle("The JWT token that you passed does not matches with the user ID that you want to create bank account!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(InvalidCurrencyCodeException.class)
    public ResponseEntity<ProblemDetail> handlerInvalidCurrencyCodeException(
            InvalidCurrencyCodeException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        problemDetail.setTitle("The currency code must follow ISO 4217");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerUsernameNotFoundException(UsernameNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("The username that you passed was not found");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(BankAccountNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerBankAccountNotFoundException(BankAccountNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("The bank account was not found");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerEmailNotFoundException(EmailNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("The email was not found");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(UnconfirmedUserException.class)
    public ResponseEntity<ProblemDetail> handlerUnconfirmedUserException(UnconfirmedUserException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
        problemDetail.setTitle("Your user aren't confirmed yet");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var fieldErrors = e.getFieldErrors()
                .stream()
                .map(f -> new InvalidParam(f.getField(), f.getDefaultMessage()))
                .toList();

        var pb = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        pb.setTitle("Your request parameters did not validate!");
        pb.setProperty("Invalid-params", fieldErrors);

        return pb;
    }

    private record InvalidParam(String name, String reason){}

    private String timeFormatted() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }
}
