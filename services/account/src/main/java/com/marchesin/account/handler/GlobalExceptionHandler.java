package com.marchesin.account.handler;

import com.marchesin.account.exception.*;
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

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handlerBusinessException(
            BusinessException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(e.getStatus(), e.getMessage());
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(e.getStatus()).body(problemDetail);
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
