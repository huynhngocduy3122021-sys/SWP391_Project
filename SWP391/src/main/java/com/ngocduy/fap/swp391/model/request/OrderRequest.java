package com.ngocduy.fap.swp391.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrderRequest {
    private BigDecimal totalAmount;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;
    private String status;
    private String paymentStatus;
    private Long memberId;
    private Long packageId;
    private Long payId;
}
