package com.marchesin.user.service;

import com.marchesin.user.domain.AuthenticatedUser;

import java.util.Optional;

public interface IdentityProvider {

    Optional<AuthenticatedUser> findByEmail(String email);
}
