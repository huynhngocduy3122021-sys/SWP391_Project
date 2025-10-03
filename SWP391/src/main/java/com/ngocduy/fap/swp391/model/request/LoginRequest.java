package com.ngocduy.fap.swp391.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequest {
    @NotEmpty
    private String email; // user đăng nhập bằng gmail
    @NotEmpty
    private String password;
}
