package com.ngocduy.fap.swp391.model.response;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ArticleResponse {
    private Long articleId;
    private String title;
    private String content;
    private String location;
    private String articleType;
    private LocalDate publicDate;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private long memberId;
    private String memberName; // To display member name directly in response
    private BigDecimal price;
    private String status;
    private long approvedAdminId;
    private String approvedAdminName; // To display admin name directly in response
    private String mainImgUrl;
}
