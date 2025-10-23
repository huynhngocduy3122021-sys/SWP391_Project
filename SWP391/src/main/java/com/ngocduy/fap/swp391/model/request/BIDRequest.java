package com.ngocduy.fap.swp391.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BIDRequest {

    @NotNull(message = "Bid amount cannot be null")
    //@JsonProperty("bidAmount") // Đảm bảo JSON key "bidAmount" map chính xác
    private Double bidAmount;

    @NotNull(message = "Auction ID cannot be null")
    //@JsonProperty("auctionId")
    private Long auctionId;

    @NotNull(message = "Member ID cannot be null")
   // @JsonProperty("member")
    private Long member;

}
