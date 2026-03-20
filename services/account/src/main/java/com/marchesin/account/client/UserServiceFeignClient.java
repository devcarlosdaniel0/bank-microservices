package com.marchesin.account.client;

import com.marchesin.account.dto.external.AuthenticatedUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserServiceFeignClient {

    @GetMapping("/api/v1/user")
    AuthenticatedUser findByEmail(@RequestParam("email") String email);
}
