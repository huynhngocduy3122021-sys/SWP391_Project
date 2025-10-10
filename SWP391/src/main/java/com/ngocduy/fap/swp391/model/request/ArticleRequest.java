package com.ngocduy.fap.swp391.model.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ArticleRequest {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    @NotBlank(message = "Location cannot be blank")
    private String location;

    @NotBlank(message = "Article type cannot be blank")
    private String articleType; //CAR_ARTICLE, MOTOR_ARTICLE, BATTERY_ARTICLE

    @NotNull(message = "Public date cannot be null")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate publicDate;

    @NotNull(message = "Member ID cannot be null")
    private Long memberId;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.00", inclusive = true, message = "Price must be zero or positive")
    private BigDecimal price;

    @NotBlank(message = "Status cannot be blank")
    private String status;

    private Long approvedAdminId;

    private String mainImgUrl;
}
