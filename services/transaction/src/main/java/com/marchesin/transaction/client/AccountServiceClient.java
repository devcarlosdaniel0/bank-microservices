package com.marchesin.transaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

    @GetMapping("/api/v1/account/find-by-user-id")
    String getAccountIdFromUserId(@RequestParam("userId") String userId);
}
