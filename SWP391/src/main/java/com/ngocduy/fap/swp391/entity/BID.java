package com.ngocduy.fap.swp391.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BID {
    //+BIDId : Long(PK), not nul
    //+ AucID : Nvarchar(FK), notnulll
    //+ MemberID : Nvarchar(FK), not null
    //+ BIDAmount : Decimal
    //+ Time : timestamp
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BIDId")
    private Long bidId;
    @Column(name = "BIDAmount")
    @NotNull
    private double bidAmount;
    @Column(name = "BidDate")
    @NotNull
    private LocalDateTime bidDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id" , nullable = false)
    @JsonIgnore
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_Id" , nullable = false)
    @JsonBackReference
    private Auction auction;
}
