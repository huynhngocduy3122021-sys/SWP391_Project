package com.ngocduy.fap.swp391.model.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ngocduy.fap.swp391.enums.ArticleStatus;
import com.ngocduy.fap.swp391.enums.ArticleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRequest {

    private Long articleId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    @NotBlank(message = "Location cannot be blank")
    private String location;

    @Enumerated(EnumType.STRING)
    private ArticleType articleType;

    @NotNull(message = "Public date cannot be null")
    private LocalDate publicDate;

    private String contactPhone;

    @NotNull(message = "Member ID cannot be null")
    private Long memberId;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.00", inclusive = false , message = "Price must be zero or positive")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private ArticleStatus status;

    private Long approvedById;

    private List<String> imageUrls; // List of image URLs, first one will be marked as main


}
