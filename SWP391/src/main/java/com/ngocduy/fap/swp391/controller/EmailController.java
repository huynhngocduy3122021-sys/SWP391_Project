package com.ngocduy.fap.swp391.controller;

import com.ngocduy.fap.swp391.model.response.EmailDetail;
import com.ngocduy.fap.swp391.service.EmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("api/email")
public class EmailController {

    @Autowired
    EmailService emailService;

    @PostMapping
    public void sendMail(@RequestBody EmailDetail emailDetail) {
        emailService.sendMailTemplate(emailDetail , "forgotPassword.html");

    }
}
