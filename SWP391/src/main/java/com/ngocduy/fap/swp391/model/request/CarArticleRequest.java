package com.ngocduy.fap.swp391.model.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CarArticleRequest {

    @NotNull(message = "Article ID is mandatory")
    private Long articleId; // The ID of the Article this car describes

    @NotBlank(message = "Brand cannot be empty")
    private String brand;

    @NotBlank(message = "Model cannot be empty")
    private String model;

    @NotNull(message = "Year cannot be empty")
    @Min(value = 1900, message = "Year must be at least 1900")
    private Integer year;

    @NotBlank(message = "Origin cannot be empty")
    private String origin;

    @NotBlank(message = "Type cannot be empty")
    private String type;

    @NotNull(message = "Number of seats cannot be empty")
    @Min(value = 1, message = "Number of seats must be at least 1")
    private Integer numberOfSeat;

    private String licensesPlate;

    private LocalDate registrationDeadline;

    private Double milesTraveled;

    private Integer warrantyPeriodMonths;
}
