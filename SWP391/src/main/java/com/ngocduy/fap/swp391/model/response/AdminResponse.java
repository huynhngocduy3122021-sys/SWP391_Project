package com.ngocduy.fap.swp391.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;


@Data
public class AdminResponse {
    private long adminId;
    private String email;
    private String name;
    private String phone;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yearOfBirth;
    private String address;
    private String sex;
    // Password is intentionally omitted for security reasons in the response
}

