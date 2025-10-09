package com.ngocduy.fap.swp391.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity

@Getter
@Setter
public class Package implements Serializable {

    @Id
    @Column(name = "PackageID", nullable = false)
    long packageId;

    @Column(name = "Name" , columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "Name can not empty!")
    String name;

    @Column(name = "NumberOfPost")
    int numberOfPost;

    @Column(name = "Description")
    String description;

    @Column(name = "Price")
    BigDecimal price;

    //Relationship
    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    List<Order> orders;

    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    List<Subscription> subscriptions;


}
