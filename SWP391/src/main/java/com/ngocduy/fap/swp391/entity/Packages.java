package com.ngocduy.fap.swp391.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
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

    @Column(name = "Name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(name = "NumberOfPost",nullable = false, columnDefinition = "NVARCHAR(255)" )
    private Integer numberOfPost;

    @Column(name = "Description", columnDefinition = "NVARCHAR(255)")
    private String description;

    @Column(name = "Price")
    private float price;

    @Column(name = "DurationDays")
    private Integer durationDays; // Duration in days (e.g., 30, 60, 90)

    @Column(name= "IsActive")
    private boolean isActive = true;
    // Relationship
    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions;

}
