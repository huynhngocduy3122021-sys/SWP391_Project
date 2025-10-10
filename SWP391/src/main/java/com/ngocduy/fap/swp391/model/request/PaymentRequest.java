package com.ngocduy.fap.swp391.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private String method;
    private String transactionCode;
    private BigDecimal amount;
    private String status;
}
