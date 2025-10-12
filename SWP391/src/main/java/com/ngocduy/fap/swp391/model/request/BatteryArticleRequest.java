package com.ngocduy.fap.swp391.model.request;


import jakarta.validation.constraints.DecimalMin;
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
public class BatteryArticleRequest extends ArticleRequest {

    @NotNull(message = "Voltage cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Voltage must be positive")
    private Double volt;

    @NotNull(message = "Capacity cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Capacity must be positive")
    private Double capacity;

    @NotNull(message = "Size cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Size must be positive")
    private Double size;

    @NotNull(message = "Weight cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be positive")
    private Double weight;

    @NotBlank(message = "Brand cannot be blank")
    private String brand;

    @NotBlank(message = "Origin cannot be blank")
    private String origin;

    @NotNull(message = "Warranty months cannot be null")
    @Min(value = 0, message = "Warranty months must be zero or positive")
    private Integer warrantyMonths;
}
