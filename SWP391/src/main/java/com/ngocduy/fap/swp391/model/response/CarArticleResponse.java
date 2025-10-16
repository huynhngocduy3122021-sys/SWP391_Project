package com.ngocduy.fap.swp391.model.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;


@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CarArticleResponse extends ArticleResponse{

    private String brand;
    private String model;
    private Integer year;
    private String origin;
    private String type;
    private Integer numberOfSeat;
    private String licensesPlate;
    private LocalDate registrationDeadline;
    private Double milesTraveled;
    private Integer warrantyPeriodMonths;

}
