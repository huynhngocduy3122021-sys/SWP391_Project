package com.ngocduy.fap.swp391.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RejectArticleRequest {
    @NotEmpty(message = "Rejection reason cannot be empty")
    private String rejectionReason;
}

