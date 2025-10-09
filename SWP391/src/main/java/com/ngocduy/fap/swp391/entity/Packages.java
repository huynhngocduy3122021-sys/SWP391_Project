package com.ngocduy.fap.swp391.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Packages implements Serializable {
    @Id
    @Column(name = "PackageID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long packageId;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "NumberOfPost")
    private Integer numberOfPost;

    @Column(name = "Description")
    private String description;

    @Column(name = "Price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name= "IsActive")
    private boolean isActive = true;
    // Relationship
    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions;

}
