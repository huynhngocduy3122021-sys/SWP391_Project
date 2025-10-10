package com.ngocduy.fap.swp391.model.response;


import com.ngocduy.fap.swp391.entity.CarArticle;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CarArticleResponse {
    private long id;
    private Long articleId;
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
    private Boolean deleted; // Include soft-deleted status in response


}
