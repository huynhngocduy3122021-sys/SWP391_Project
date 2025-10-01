package com.ngocduy.fap.swp391.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Entity
@Getter
@Setter
public class Member{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long memberId;

    @NotEmpty(message = "Name can not empty!")
    String name;

    @NotEmpty(message = "Address cannot be empty!")
    String address;

    @Past(message = "Invalid date of birth!")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    LocalDate yearOfBirth;

    @Pattern(regexp = "(03|05|07|08|09|012|016|018|019)[0-9]{8}$", message = "Phone invalid!")
    @NotEmpty(message = "Phone cannot be empty!")
    String phone;

    @Email
    @NotEmpty(message = "Email cannot be empty!")
    String email;

    String status;

    String sex;

    @NotEmpty(message = "password can not empty!")
    String password;


}
