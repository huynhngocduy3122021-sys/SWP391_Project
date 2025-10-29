package com.ngocduy.fap.swp391.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    private String method = "VNPAY"; // Default to VNPAY
    
    private String bankCode; // Optional: specific bank for VNPAY (e.g., NCB, VIETCOMBANK)
}
