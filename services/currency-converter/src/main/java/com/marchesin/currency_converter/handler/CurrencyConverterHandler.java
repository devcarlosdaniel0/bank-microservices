package com.marchesin.currency_converter.handler;

import com.marchesin.currency_converter.exception.CurrencyNotFoundException;
import com.marchesin.currency_converter.exception.InsufficientAmountValueException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class CurrencyConverterHandler {
    @ExceptionHandler(InsufficientAmountValueException.class)
    public ResponseEntity<ProblemDetail> handlerInsufficientAmountValueException(
            InsufficientAmountValueException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

    @ExceptionHandler(CurrencyNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerCurrencyNotFoundException(
            CurrencyNotFoundException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ProblemDetail> handlerFeignException(
            FeignException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatusCode.valueOf(e.status()));
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatusCode.valueOf(e.status())).body(problemDetail);
    }

    private String timeFormatted() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }
}
