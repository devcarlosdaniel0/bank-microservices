package com.marchesin.currency_converter.handler;

import com.marchesin.currency_converter.exception.ApiException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ProblemDetail> handlerApiException(
            ApiException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(e.getStatus(), e.getMessage());
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(e.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handlerConstraintViolation(
            ConstraintViolationException e
    ) {
        String detail = e.getConstraintViolations()
                .stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("Invalid request parameter");

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                detail
        );

        problem.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.badRequest().body(problem);
    }


    private String timeFormatted() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }
}
