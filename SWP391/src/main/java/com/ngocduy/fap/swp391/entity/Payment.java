package com.ngocduy.fap.swp391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @Column(name = "PayID")
    private Long payId;

    @Column(name = "Method")
    private String method;

    @Column(name = "TransactionCode")
    private String transactionCode;

    @Column(name = "Amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "PaymentDate")
    private LocalDateTime paymentDate;

    @Column(name = "Status")
    private String status;

    @Column(name = "IsDeleted")
    private boolean isDeleted = false;

    // Relationship
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<Order> orders;
}

