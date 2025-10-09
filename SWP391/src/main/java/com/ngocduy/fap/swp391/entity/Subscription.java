package com.ngocduy.fap.swp391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Subscription implements Serializable {

    @EmbeddedId
    private SubscriptionId id;

    @Column (name = "StartDate", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    LocalDateTime startDate;

    @Column (name = "EndDate")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    LocalDateTime endDate;

    @Column (name = "Status")
    String status;

    //Relationship
    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "MemberID")
    Member member;

    @ManyToOne
    @MapsId ("packageId")
    @JoinColumn(name = "PackageID")
    Package pkg;


}
