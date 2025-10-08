package com.ngocduy.fap.swp391.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "articleId")
    private long articleId;

    @Column(name = "title" , columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "title cannot be empty!")
    private String title;

    @Column(name = "provincesAndCities" , columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "provinces and cities cannot be empty!")
    private String provincesAndCities;

    @Column(name = "articleType" , columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "article type cannot be empty!")
    private String articleType;

    @Column(name = "date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotEmpty(message = "date cannot be empty!")
    private LocalDate date;

    @NotEmpty(message = "price cannot be empty!")
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member memberId;
}
