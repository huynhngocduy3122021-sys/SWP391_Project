package com.ngocduy.fap.swp391.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long payId;
    private String method;
    private String transactionCode;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String status;
}
