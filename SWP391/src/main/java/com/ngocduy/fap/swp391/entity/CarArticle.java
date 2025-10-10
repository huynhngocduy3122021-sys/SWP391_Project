package com.ngocduy.fap.swp391.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class CarArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_article_id")
    private long id;

    @Column(name = "brand", columnDefinition = "NVARCHAR(255)", nullable = false)
    @NotBlank(message = "Brand cannot be empty!")
    private String brand;

    @Column(name = "model", columnDefinition = "NVARCHAR(255)", nullable = false)
    @NotBlank(message = "Model cannot be empty!")
    private String model;

    @Column(name = "year", nullable = false)
    @NotNull(message = "Year cannot be empty!")
    @Min(value = 1900, message = "Year must be at least 1900")
    private Integer year;

    @Column(name = "origin", columnDefinition = "NVARCHAR(255)", nullable = false)
    @NotBlank(message = "Origin cannot be empty!")
    private String origin;

    @Column(name = "type", columnDefinition = "NVARCHAR(255)", nullable = false)
    @NotBlank(message = "Type cannot be empty!")
    private String type;

    @Column(name = "number_of_seat", nullable = false)
    @NotNull(message = "Number of seats cannot be empty!")
    @Min(value = 1, message = "Number of seats must be at least 1")
    private Integer numberOfSeat;

    @Column(name = "licenses_plate", columnDefinition = "NVARCHAR(255)", unique = true)
    private String licensesPlate;

    @Column(name = "registration_deadline")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate registrationDeadline;

    @Column(name = "miles_traveled")
    private Double milesTraveled;

    @Column(name = "warranty_period_months")
    private Integer warrantyPeriodMonths;

    @Column(name = "is_deleted")
    private boolean deleted = false;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false, unique = true)
    private Article article; // No change here, still links to Article

}
