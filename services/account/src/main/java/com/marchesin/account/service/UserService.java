package com.marchesin.account.service;

import com.marchesin.account.client.UserServiceFeignClient;
import com.marchesin.account.dto.external.AuthenticatedUser;
import com.marchesin.account.exception.feign.UserNotFound;
import com.marchesin.account.exception.feign.UserServiceUnavailable;
import feign.FeignException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserServiceFeignClient client;

    public UserService(UserServiceFeignClient client) {
        this.client = client;
    }

    public AuthenticatedUser findByEmail(String email) {
        AuthenticatedUser user;

        try {
            user = client.findByEmail(email);
        } catch (FeignException.NotFound e) {
            throw new UserNotFound("User not found");
        } catch (FeignException e) {
            throw new UserServiceUnavailable("User service unavailable");
        }

        return user;
    }
}
