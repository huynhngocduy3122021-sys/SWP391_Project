package com.ngocduy.fap.swp391.model.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BIDResponse {


    private double presentPrice;
    private double YourPrice;
}
