package com.ngocduy.fap.swp391.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MotorArticle {

    @Id
    @Column(name = "id")
    private Long id; // This will be the ArticleID


    @Column(name = "brand", columnDefinition = "NVARCHAR(255)")
    @NotBlank(message = "Brand cannot be blank")
    private String brand;

    @Column(name = "year")
    @NotNull(message = "Year cannot be null")
    @Min(value = 1900, message = "Year must be after 1900")
    private Integer year;

    @Column(name = "vehicle_capacity")
    @NotNull(message = "Vehicle capacity cannot be null")
    @Min(value = 1, message = "Vehicle capacity must be at least 1")
    private Integer vehicleCapacity;

    @Column(name = "licenses_plate", unique = true, columnDefinition = "NVARCHAR(255)")
    @NotBlank(message = "Licenses plate cannot be blank")
    private String licensesPlate;

    @Column(name = "origin", columnDefinition = "NVARCHAR(255)")
    @NotBlank(message = "Origin cannot be blank")
    private String origin;

    @Column(name = "miles_traveled")
    @NotNull(message = "Miles traveled cannot be null")
    @Min(value = 0, message = "Miles traveled must be zero or positive")
    private Double milesTraveled;

    @Column(name = "warranty_months")
    @NotNull(message = "Warranty months cannot be null")
    @Min(value = 0, message = "Warranty months must be zero or positive")
    private Integer warrantyMonths;


    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Indicates that the primary key of this entity is also a foreign key
    @JoinColumn(name = "article_id", referencedColumnName = "article_id") // Maps `id` to `article_id` in Article
    private Article article;
}
