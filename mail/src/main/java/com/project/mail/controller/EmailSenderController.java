package com.project.mail.controller;

import com.project.mail.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailSenderController {
    private final EmailSenderService emailSenderService;

    @PostMapping("/email/send")
    public ResponseEntity<String> sendEmail(@RequestParam String toEmail,
                                          @RequestParam String subject,
                                          @RequestParam String body) {
        emailSenderService.sendEmail(toEmail, subject, body);
        return new ResponseEntity<>("E-mail sent successfully!", HttpStatus.OK);
    }
}
