package com.project.auth.security.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mail-api")
public interface EmailClient {
    @PostMapping("/email/send")
    void sendEmail(@RequestParam String toEmail,
                   @RequestParam String subject,
                   @RequestParam String body);
}
