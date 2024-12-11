package com.project.auth.security.exception;

import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ErrorWhileGeneratingTokenJwtException extends RuntimeException {
    public ErrorWhileGeneratingTokenJwtException(String message) {
        super(message);
    }
}
