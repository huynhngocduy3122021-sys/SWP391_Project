package com.ngocduy.fap.swp391.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrderResponse {
    private Long orderId;
    private BigDecimal totalAmount;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;
    private String status;
    private String paymentStatus;
    private Long memberId;
    private String memberName;
    private Long packageId;
    private String packageName;
    private Long payId;
}
