package com.ngocduy.fap.swp391.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @NotNull(message = "Member ID is required")
    private Long memberId;
    
    @NotNull(message = "Package ID is required")
    private Long packageId;
}
