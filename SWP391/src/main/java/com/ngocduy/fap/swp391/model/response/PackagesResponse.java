package com.ngocduy.fap.swp391.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PackagesResponse {
    long packageId;
     String name;
     int numberOfPost;
     String description;
     BigDecimal price;
}
