package com.ngocduy.fap.swp391.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aucID")
    private Long aucID;
    @NotBlank(message = "title cannot be empty!")
    @Column(name = "title" , nullable = false , columnDefinition = "NVARCHAR(255)")
    private String title;
    @NotBlank(message = "description cannot be empty!")
    @Column(name = "description" , nullable = false , columnDefinition = "NVARCHAR(255)")
    private String description;
   @NotNull(message = "start time cannot be empty!")
    @Column(name = "startTime" , nullable = false)
   @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startTime;
   @NotNull(message = "end time cannot be empty!")
    @Column(name = "endTime" , nullable = false)
   @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endTime;
    @NotBlank(message = "status cannot be empty!")
    @Column(name = "status" , nullable = false , columnDefinition = "NVARCHAR(255)")
    private String status;
    @NotNull(message = "price cannot be empty!")
    @Column(name = "price" , nullable = false)
    private Double price;
    @NotNull(message = "increment cannot be empty!")
    @Column(name = "increment" , nullable = false)
    private Double increment;

    //soft delete
    @Column(name = "is_deleted")
    private boolean deleted = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member memberId;

}
