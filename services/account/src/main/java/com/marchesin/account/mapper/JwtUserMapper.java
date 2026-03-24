package com.marchesin.account.mapper;

import com.marchesin.account.dto.external.AuthUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUserMapper {

    public AuthUser from(Jwt jwt) {
        return new AuthUser(
                jwt.getSubject(),
                jwt.getClaim("email"),
                jwt.getClaim("name"),
                jwt.getClaim("email_verified")
        );
    }
}
