package com.ngocduy.fap.swp391.model.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MotorArticleResponse extends ArticleResponse{

    private String brand;
    private Integer year;
    private Integer vehicleCapacity;
    private String licensesPlate;
    private String origin;
    private Double milesTraveled;
    private Integer warrantyMonths;
}
