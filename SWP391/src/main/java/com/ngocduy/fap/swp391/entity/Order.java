package com.ngocduy.fap.swp391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="OrderID", nullable = false)
    Long orderId;

    @Column (name= "TotalAmount", precision = 10, scale = 2)
    BigDecimal totalAmount;

    @Column(name = "Date", nullable = false, updatable = false)
    LocalDateTime Date = LocalDateTime.now();

    @Column(name = "Status")
    String status;

    @Column(name = "PaymentStatus")
    String paymentStatus;

    //Relationship
    @ManyToOne
    @JoinColumn (name = "MemberID" ,nullable = false)
    Member member;

    @ManyToOne
    @JoinColumn (name = "PackageID" ,nullable = false)
    Package pkg;

    @ManyToOne
    @JoinColumn (name = "PayID" ,nullable = false)
    Payment payment;

}
