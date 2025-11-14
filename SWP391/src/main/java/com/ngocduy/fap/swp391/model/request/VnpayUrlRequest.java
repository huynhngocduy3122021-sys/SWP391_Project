package com.ngocduy.fap.swp391.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VnpayUrlRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;
}


