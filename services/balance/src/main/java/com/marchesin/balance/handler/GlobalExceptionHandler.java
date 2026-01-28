package com.marchesin.balance.handler;

import com.marchesin.balance.exception.AmountCantBeNegativeOrZero;
import com.marchesin.balance.exception.BalanceAlreadyExists;
import com.marchesin.balance.exception.BalanceNotFound;
import com.marchesin.balance.exception.InsufficientFunds;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BalanceAlreadyExists.class)
    public ResponseEntity<ProblemDetail> handlerBalanceAlreadyExists(BalanceAlreadyExists e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(BalanceNotFound.class)
    public ResponseEntity<ProblemDetail> handlerBalanceNotFound(BalanceNotFound e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(AmountCantBeNegativeOrZero.class)
    public ResponseEntity<ProblemDetail> handlerAmountCantBeNegativeOrZero(AmountCantBeNegativeOrZero e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

    @ExceptionHandler(InsufficientFunds.class)
    public ResponseEntity<ProblemDetail> handlerInsufficientFunds(InsufficientFunds e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var fieldErrors = e.getFieldErrors()
                .stream()
                .map(f -> new InvalidParam(f.getField(), f.getDefaultMessage()))
                .toList();

        var pb = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        pb.setProperty("Invalid-params", fieldErrors);
        pb.setProperty("timestamp", timeFormatted());

        return pb;
    }

    private record InvalidParam(String name, String reason){}

    private String timeFormatted() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }
}
