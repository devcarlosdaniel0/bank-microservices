package com.marchesin.account.mapper;

import com.marchesin.account.dto.external.AuthenticatedUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUserMapper {

    public AuthenticatedUser from(Jwt jwt) {
        return new AuthenticatedUser(
                jwt.getSubject(),
                jwt.getClaim("email"),
                jwt.getClaim("name"),
                jwt.getClaim("email_verified")
        );
    }
}
