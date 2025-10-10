package com.ngocduy.fap.swp391.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Article {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "title", columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "title cannot be empty!")
    private String title;

    @Column(name = "content", columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "content cannot be empty!")
    private String content;

    @Column(name = "location", columnDefinition = "NVARCHAR(255)")
    @NotEmpty(message = "location cannot be empty!")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "article_type", length = 50, columnDefinition = "NVARCHAR(255)")
    @NotNull(message = "article type cannot be empty!")
    private ArticleType articleType;

    @Column(name = "public_date", nullable = false)
    private LocalDate publicDate;

    @CreationTimestamp // Automatically sets creation timestamp
    @Column(name = "create_at", updatable = false) // updatable=false means it's set once
    private LocalDateTime createAt;

    @UpdateTimestamp // Automatically updates timestamp on entity modification
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "price", precision = 10, scale = 2) // Precision and scale for decimal
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, columnDefinition = "NVARCHAR(255)")
    @NotNull(message = "status cannot be empty!")
    private ArticleStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_admin_id")
    private Admin approvedAdmin;

    @Column(name = "main_img_url", length = 2048) // Common max length for URLs
    private String mainImgUrl;

    @Column(name = "is_deleted")
    private boolean deleted = false;


    public enum ArticleType {
        CAR_ARTICLE,
        MOTOR_ARTICLE,
        BATTERY_ARTICLE
    }

    public enum ArticleStatus {
        DRAFT,
        PENDING_APPROVAL,
        APPROVED,
        REJECTED,
        ARCHIVED
    }


}
