package com.ngocduy.fap.swp391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PayID")
    private Long payId;

    @Column(name = "Method")
    private String method;

    @Column(name = "TransactionCode")
    private String transactionCode;

    @Column(name = "Amount")
    private float amount;

    @Column(name = "PaymentDate")
    private LocalDateTime paymentDate;

    @Column(name = "Status")
    private String status = "PENDING";

    @Column(name = "IsDeleted")
    private boolean isDeleted = false;

    // VNPAY fields
    @Column(name = "VnpTxnRef", unique = true)
    private String vnpTxnRef; // Unique transaction reference for each payment attempt

    @Column(name = "VnpTransactionNo")
    private String vnpTransactionNo; // VNPAY's transaction number

    @Column(name = "VnpBankCode")
    private String vnpBankCode;

    @Column(name = "VnpCardType")
    private String vnpCardType;

    @Column(name = "VnpPayDate")
    private String vnpPayDate;

    @Column(name = "VnpResponseCode")
    private String vnpResponseCode; // 00 = success, others = fail

    @Column(name = "VnpSecureHash", length = 512)
    private String vnpSecureHash;

    // Relationship: Payment belongs to Order
    @ManyToOne
    @JoinColumn(name = "OrderID", nullable = false)
    private Order order;
}

