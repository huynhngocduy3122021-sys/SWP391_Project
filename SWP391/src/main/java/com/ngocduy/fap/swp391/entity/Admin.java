package com.ngocduy.fap.swp391.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Entity
@Getter
@Setter
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adminId")
    private long adminId;

    @Column(name = "email" , columnDefinition = "NVARCHAR(255)", unique = true)
    @Email
    @NotEmpty(message = "Email cannot be empty!")
    private String email;

    @Column(name = "name" , columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "name cannot be empty!")
    private String name;

    @Column(name = "phone", unique = true)
    @Pattern(regexp = "(03|05|07|08|09|012|016|018|019)[0-9]{8}$", message = "Phone invalid!")
    @NotEmpty(message = "Phone cannot be empty!" )
    private String phone;

    @Column(name = "yearOfBirth")
    @Past(message = "Invalid date of birth!")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yearOfBirth;

    @Column(name = "address" , columnDefinition = "NVARCHAR(255)")
    private String address;

    @Column(name = "sex")
    private String sex;

    @Column(name = "password")
    @NotEmpty(message = "password can not empty!")
    private String password;

    @OneToMany(mappedBy = "adminId")
    @JsonIgnore
    List<Member> members;


    @OneToMany(mappedBy = "approvedAdmin", cascade = CascadeType.ALL)
    @JsonIgnore
    List<Article> approcedArticles;
}
