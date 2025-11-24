package com.ngocduy.fap.swp391.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotEmpty(message = "Token cannot be empty")
    private String token;

    @NotEmpty(message = "New password cannot be empty")
    private String newPassword;
}

