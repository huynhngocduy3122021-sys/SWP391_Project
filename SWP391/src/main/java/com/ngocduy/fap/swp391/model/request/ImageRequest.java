package com.ngocduy.fap.swp391.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {
    
    @NotBlank(message = "Image URL cannot be blank")
    private String url;
    
    private boolean isMain = false;
}
