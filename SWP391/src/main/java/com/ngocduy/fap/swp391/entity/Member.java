package com.ngocduy.fap.swp391.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ngocduy.fap.swp391.enums.MemberStatus;
import com.ngocduy.fap.swp391.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Setter
public class Member implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberID")
    private long memberId;

    @Column(name = "name" , columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "Name can not empty!")
    private String name;

    @Column(name = "address" , columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "Address cannot be empty!")
    private String address;

    @Column(name = "yearOfBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate yearOfBirth;

    @Column(name = "phone", unique = true)
    @Pattern(regexp = "(03|05|07|08|09|012|016|018|019)[0-9]{8}$", message = "Phone invalid!")
    @NotEmpty(message = "Phone cannot be empty!" )
    private String phone;

    @Column(name = "email" , columnDefinition = "NVARCHAR(255)", unique = true)
    @Email
    @NotEmpty(message = "Email cannot be empty!")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , columnDefinition = "NVARCHAR(255)")
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column(name = "sex")
    private String sex;

    @Column(name = "password")
    @NotEmpty(message = "password can not empty!")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")  
    private Role role = Role.MEMBER;

    // Soft_Deleted
    @Column(name = "is_deleted")
    private boolean deleted = false;

    // Password Reset Token
    @Column(name = "reset_token", columnDefinition = "NVARCHAR(255)")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private java.time.LocalDateTime resetTokenExpiry;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }



    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    @JsonIgnore
    List<Article> articles;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> orders;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Subscription> subscriptions;

}
