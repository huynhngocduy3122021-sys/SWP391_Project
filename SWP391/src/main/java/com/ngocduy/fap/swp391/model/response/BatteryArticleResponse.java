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
public class BatteryArticleResponse extends ArticleResponse {

    private Double volt;
    private Double capacity;
    private Double size;
    private Double weight;
    private String brand;
    private String origin;
    private Integer warrantyMonths;

}
