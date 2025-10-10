package com.ngocduy.fap.swp391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Subscription {
    @EmbeddedId
    private SubscriptionId id;

    @Column(name = "StartDate")
    private LocalDateTime startDate;

    @Column(name = "EndDate")
    private LocalDateTime endDate;

    @Column(name = "Status")
    private String status;

    @Column(name = "IsDeleted")
    private boolean isDeleted = false;

    // Relationships
    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "MemberID")
    private Member member;

    @ManyToOne
    @MapsId("packageId")
    @JoinColumn(name = "PackageID")
    private Packages pkg;
}
