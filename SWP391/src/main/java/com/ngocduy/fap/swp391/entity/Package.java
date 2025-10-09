package com.ngocduy.fap.swp391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
public class Package implements Serializable {
    @Id
    @Column(name = "PackageID", nullable = false)
    private long packageId;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "NumberOfPost")
    private Integer numberOfPost;

    @Column(name = "Description")
    private String description;

    @Column(name = "Price")
    private Double price;

    // Relationship
    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions;

}
