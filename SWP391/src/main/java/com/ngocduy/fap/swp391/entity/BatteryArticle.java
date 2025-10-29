package com.ngocduy.fap.swp391.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BatteryArticle {

    @Id
    @Column(name = "id")
    private Long id; // This will be the ArticleID

    @Column(name = "volt") // Confirmed as Double
    @NotNull(message = "Voltage cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Voltage must be positive")
    private Double volt;

    @Column(name = "capacity") // Confirmed as Double
    @NotNull(message = "Capacity cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Capacity must be positive")
    private Double capacity;

    @Column(name = "size")
    @NotNull(message = "Size cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Size must be positive")
    private Double size;

    @Column(name = "weight")
    @NotNull(message = "Weight cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be positive")
    private Double weight;

    @Column(name = "brand", columnDefinition = "NVARCHAR(255)")
    @NotBlank(message = "Brand cannot be blank")
    private String brand;

    @Column(name = "origin", columnDefinition = "NVARCHAR(255)")
    @NotBlank(message = "Origin cannot be blank")
    private String origin;

    @Column(name = "warranty_months")
    @NotNull(message = "Warranty months cannot be null")
    @Min(value = 0, message = "Warranty months must be zero or positive")
    private Integer warrantyMonths;


    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Indicates that the primary key of this entity is also a foreign key
    @JoinColumn(name = "article_id", referencedColumnName = "article_id") // Maps `id` to `article_id` in Article
    private Article article;
}
