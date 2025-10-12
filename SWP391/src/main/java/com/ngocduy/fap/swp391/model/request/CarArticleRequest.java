package com.ngocduy.fap.swp391.model.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CarArticleRequest extends ArticleRequest{

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

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate registrationDeadline;

    private Double milesTraveled;

    private Integer warrantyPeriodMonths;
}
