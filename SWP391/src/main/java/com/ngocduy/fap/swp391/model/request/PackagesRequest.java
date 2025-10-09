package com.ngocduy.fap.swp391.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PackagesRequest {
    private String name;
    private int numberOfPost;
    private String description;
    private BigDecimal price;

}
