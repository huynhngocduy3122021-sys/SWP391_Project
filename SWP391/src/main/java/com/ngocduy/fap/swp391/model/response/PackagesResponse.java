package com.ngocduy.fap.swp391.model.response;

import lombok.Data;

@Data
public class PackagesResponse {
    long packageId;
    String name;
    int numberOfPost;
    String description;
    float price;
    Integer durationDays;
    boolean isActive;
}
