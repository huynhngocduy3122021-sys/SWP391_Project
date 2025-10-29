package com.ngocduy.fap.swp391.model.request;

import lombok.Data;

@Data
public class PackagesRequest {
    private String name;
    private int numberOfPost;
    private String description;
    private float price;
    private Integer durationDays; // Duration in days
}
