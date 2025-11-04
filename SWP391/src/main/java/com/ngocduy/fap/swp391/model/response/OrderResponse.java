package com.ngocduy.fap.swp391.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ngocduy.fap.swp391.enums.OrderStatus;
import com.ngocduy.fap.swp391.enums.PaymentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderResponse {
    private Long orderId;
    private float totalAmount;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private Long memberId;
    private String memberName;
    private Long packageId;
    private String packageName;
}
