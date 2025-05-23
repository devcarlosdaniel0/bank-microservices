package com.project.auth.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.auth.security.exception.ErrorWhileGeneratingTokenJwtException;
import com.project.auth.security.exception.InvalidOrExpiredTokenException;
import com.project.core.domain.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
public class TokenJwtService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(UserEntity user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    .withClaim("userId", user.getId())
                    .withClaim("username", user.getUsername())
                    .withExpiresAt(generateExpirationTime())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new ErrorWhileGeneratingTokenJwtException("Error while generating token JWT");
        }
    }

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            throw new InvalidOrExpiredTokenException("Invalid or expired token JWT");
        }
    }

    private Instant generateExpirationTime() {
        return ZonedDateTime.now(ZoneOffset.UTC).plusHours(1).toInstant();
    }
}