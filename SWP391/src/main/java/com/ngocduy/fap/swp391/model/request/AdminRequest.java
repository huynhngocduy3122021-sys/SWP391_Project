package com.ngocduy.fap.swp391.model.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AdminRequest {

    @Email(message = "Invalid email format!")
    @NotEmpty(message = "Email cannot be empty!")
    private String email;

    @NotEmpty(message = "Name cannot be empty!")
    private String name;

    @Pattern(regexp = "^(03|05|07|08|09|012|016|018|019)[0-9]{8}$", message = "Phone number invalid!")
    @NotEmpty(message = "Phone cannot be empty!" )
    private String phone;

    @Past(message = "Date of birth cannot be in the future!")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yearOfBirth;

    private String address;

    @NotEmpty(message = "Sex cannot be empty!")
    private String sex;

    @NotEmpty(message = "Password cannot be empty!")
    private String password;
}
