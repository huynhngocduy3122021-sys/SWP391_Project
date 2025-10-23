package com.ngocduy.fap.swp391.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionResponse {
    private Long memberId;
    private String memberName;
    private Long packageId;
    private String packageName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}
