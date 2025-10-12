package com.ngocduy.fap.swp391.model.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MotorArticleRequest extends ArticleRequest{

    @NotBlank(message = "Brand cannot be blank")
    private String brand;

    @NotNull(message = "Year cannot be null")
    @Min(value = 1900, message = "Year must be after 1900")
    private Integer year;

    @NotNull(message = "Vehicle capacity cannot be null")
    @Min(value = 1, message = "Vehicle capacity must be at least 1")
    private Integer vehicleCapacity;

    @NotBlank(message = "Licenses plate cannot be blank")
    private String licensesPlate;

    @NotBlank(message = "Origin cannot be blank")
    private String origin;

    @NotNull(message = "Miles traveled cannot be null")
    @Min(value = 0, message = "Miles traveled must be zero or positive")
    private Double milesTraveled;

    @NotNull(message = "Warranty months cannot be null")
    @Min(value = 0, message = "Warranty months must be zero or positive")
    private Integer warrantyMonths;
}
