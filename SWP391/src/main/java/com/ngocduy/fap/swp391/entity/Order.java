package com.ngocduy.fap.swp391.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "Orders")
public class Order implements Serializable {
    @Id
    @Column(name = "OrderID")
    private Long orderId;

    @Column(name = "TotalAmount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "Date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    @Column(name = "Status")
    private String status;

    @Column(name = "PaymentStatus")
    private String paymentStatus;

    @Column(name = "IsDeleted")
    private boolean isDeleted = false;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "MemberID", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "PackageID", nullable = false)
    private Packages pkg;

    @ManyToOne
    @JoinColumn(name = "PayID", nullable = false)
    private Payment payment;
}
