package com.ngocduy.fap.swp391.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private Long imageId;
    private String url;
    private boolean isMain;
}
