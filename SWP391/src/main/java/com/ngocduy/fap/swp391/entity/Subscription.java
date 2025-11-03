package com.ngocduy.fap.swp391.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ngocduy.fap.swp391.enums.SubscriptionStatus;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy'T'HH:mm:ss")
    private LocalDateTime startDate;

    @Column(name = "EndDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy'T'HH:mm:ss")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(name = "RemainingPosts")
    private Integer remainingPosts; // Number of posts remaining for this subscription

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
