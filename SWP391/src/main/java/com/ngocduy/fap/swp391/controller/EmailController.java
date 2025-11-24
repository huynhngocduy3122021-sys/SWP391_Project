package com.ngocduy.fap.swp391.controller;

import com.ngocduy.fap.swp391.model.response.EmailDetail;
import com.ngocduy.fap.swp391.service.EmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("api/email")
public class EmailController {

    @Autowired
    EmailService emailService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendMail(@RequestBody EmailDetail emailDetail) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate input
            if (emailDetail == null) {
                response.put("success", false);
                response.put("message", "Email detail is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (emailDetail.getRecipient() == null || emailDetail.getRecipient().isEmpty()) {
                response.put("success", false);
                response.put("message", "Recipient email is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Send email
            boolean sent = emailService.sendMailTemplate(emailDetail);
            
            if (sent) {
                response.put("success", true);
                response.put("message", "Email sent successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to send email. Please check server logs for details.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
