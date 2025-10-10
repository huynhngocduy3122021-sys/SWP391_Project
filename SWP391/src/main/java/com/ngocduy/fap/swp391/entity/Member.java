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
import org.springframework.security.core.GrantedAuthority;
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
    @Past(message = "Invalid date of birth!")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yearOfBirth;

    @Column(name = "phone", unique = true)
    @Pattern(regexp = "(03|05|07|08|09|012|016|018|019)[0-9]{8}$", message = "Phone invalid!")
    @NotEmpty(message = "Phone cannot be empty!" )
    private String phone;

    @Column(name = "email" , columnDefinition = "NVARCHAR(255)", unique = true)
    @Email
    @NotEmpty(message = "Email cannot be empty!")
    private String email;

    @Column(name = "status" , columnDefinition = "NVARCHAR(255)")
    private String status;

    @Column(name = "sex")
    private String sex;

    @Column(name = "password")
    @NotEmpty(message = "password can not empty!")
    private String password;

    // Soft_Deleted
    @Column(name = "is_deleted")
    private boolean deleted = false;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }



    @OneToMany(mappedBy = "memberId")
    @JsonIgnore
    List<Article> articles;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin adminId;

    @OneToMany(mappedBy = "memberId")
    @JsonIgnore
    private List<Auction> auctions;

    @OneToMany(mappedBy = "member")
    @JsonIgnore
    private List<BID> bids = new ArrayList<>();
}
