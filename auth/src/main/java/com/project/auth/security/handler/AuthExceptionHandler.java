package com.project.auth.security.handler;

import com.project.auth.security.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handlerEmailAlreadyExistsException(
            EmailAlreadyExistsException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("This user already has a email registered!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerEmailNotFoundException(
            EmailNotFoundException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("The email was not found!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(ConfirmationTokenNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlerConfirmationTokenNotFoundException(
            ConfirmationTokenNotFoundException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("The confirmation token is invalid!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(ConfirmationTokenExpiredException.class)
    public ResponseEntity<ProblemDetail> handlerConfirmationTokenExpiredException(
            ConfirmationTokenExpiredException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("The confirmation token is expired!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(AccountAlreadyConfirmedException.class)
    public ResponseEntity<ProblemDetail> handlerAccountAlreadyConfirmedException(
            AccountAlreadyConfirmedException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("This account it's already confirmed!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(ErrorWhileGeneratingTokenJwtException.class)
    public ResponseEntity<ProblemDetail> handlerErrorWhileGeneratingTokenJwtException(
            ErrorWhileGeneratingTokenJwtException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        problemDetail.setTitle("A error occurred while generating token JWT!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

    @ExceptionHandler(InvalidOrExpiredTokenException.class)
    public ResponseEntity<ProblemDetail> handlerInvalidOrExpiredTokenException(
            InvalidOrExpiredTokenException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
        problemDetail.setTitle("Your token has expired or its invalid!");
        problemDetail.setProperty("timeStamp", timeFormatted());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    private String timeFormatted() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }
}
