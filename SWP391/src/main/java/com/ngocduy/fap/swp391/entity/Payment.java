package com.ngocduy.fap.swp391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Payment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PayID", nullable = false)
    long payId;

    @Column(name = "Method", columnDefinition = "VARCHAR(255)")
    String method;

    @Column(name = "TransactionCode")
    String transactionCode;

    @Column(name = "Amount", precision = 10, scale = 2)
    BigDecimal amount;

    @Column(name ="PaymentDate", nullable = false, updatable = false)
    LocalDateTime paymentDate = LocalDateTime.now();

    @Column(name = "Status")
    String status;

    //Relationship
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    List<Order> orders;
}
