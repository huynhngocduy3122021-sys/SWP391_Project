package com.ngocduy.fap.swp391.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BIDRequest {
    @NotNull
     Double BIDAmount;
    @NotNull
    Long auctionId;

}
