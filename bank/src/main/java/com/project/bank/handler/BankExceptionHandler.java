package com.project.bank.handler;

import com.project.bank.exception.BankAccountIdNotFoundException;
import com.project.bank.exception.UserAlreadyHasBankAccountException;
import com.project.bank.exception.UserIdNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class BankExceptionHandler {

    @ExceptionHandler(UserAlreadyHasBankAccountException.class)
    public ResponseEntity<ProblemDetail> handlerUserAlreadyHasBankAccountException(
            UserAlreadyHasBankAccountException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        problemDetail.setTitle("This user already has a bank account registered!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
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

    private String timeFormatted() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }
}
