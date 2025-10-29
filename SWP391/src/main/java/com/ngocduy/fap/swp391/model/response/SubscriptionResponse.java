package com.ngocduy.fap.swp391.model.response;

import com.ngocduy.fap.swp391.enums.SubscriptionStatus;
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
    private SubscriptionStatus status;
    private Integer remainingPosts;
}
