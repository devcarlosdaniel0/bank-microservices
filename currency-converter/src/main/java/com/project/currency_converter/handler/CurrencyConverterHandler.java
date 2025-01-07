package com.project.currency_converter.handler;

import com.project.currency_converter.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class CurrencyConverterHandler {
    @ExceptionHandler(InsufficientAmountValueException.class)
    public ResponseEntity<ProblemDetail> handlerInsufficientAmountValueException(
            InsufficientAmountValueException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        problemDetail.setTitle("The amount value of conversion must be greater than zero!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

    @ExceptionHandler(CurrencyNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerCurrencyNotFoundException(
            CurrencyNotFoundException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("The symbols of currencies was not found");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ProblemDetail> handlerExternalApiException(
            ExternalApiException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        problemDetail.setTitle("An error occurred while trying to communicate with invertexto API");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(InvalidSyntaxException.class)
    public ResponseEntity<ProblemDetail> handlerInvalidSyntaxException(
            InvalidSyntaxException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        problemDetail.setTitle("The symbols must consist of 3 characters followed by an underscore (_) and then another 3 characters");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ProblemDetail> handlerTimeoutException(
            TimeoutException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.REQUEST_TIMEOUT, e.getMessage());
        problemDetail.setTitle("Timeout error");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(problemDetail);
    }

    private String timeFormatted() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }
}
