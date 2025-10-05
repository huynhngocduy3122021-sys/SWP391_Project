package com.ngocduy.fap.swp391.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Setter
public class Member implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberID")
    long memberId;

    @Column(name = "name" , columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "Name can not empty!")
    String name;

    @Column(name = "address" , columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "Address cannot be empty!")
    String address;

    @Column(name = "yearOfBirth")
    @Past(message = "Invalid date of birth!")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    LocalDate yearOfBirth;

    @Column(name = "phone")
    @Pattern(regexp = "(03|05|07|08|09|012|016|018|019)[0-9]{8}$", message = "Phone invalid!")
    @NotEmpty(message = "Phone cannot be empty!" )
    String phone;

    @Column(name = "email" , columnDefinition = "NVARCHAR(255)" )
    @Email
    @NotEmpty(message = "Email cannot be empty!")
    String email;

    @Column(name = "status" , columnDefinition = "NVARCHAR(255)")
    String status;

    @Column(name = "sex" , columnDefinition = "NVARCHAR(255)")
    String sex;

    @Column(name = "password" , columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "password can not empty!")
    String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

}
