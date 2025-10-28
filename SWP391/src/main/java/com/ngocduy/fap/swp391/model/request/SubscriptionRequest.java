package com.ngocduy.fap.swp391.model.request;

import com.ngocduy.fap.swp391.enums.SubscriptionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionRequest {
    private Long memberId;
    private Long packageId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SubscriptionStatus status;
}
