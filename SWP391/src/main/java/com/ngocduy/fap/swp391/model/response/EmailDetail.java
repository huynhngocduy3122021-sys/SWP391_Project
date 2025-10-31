package com.ngocduy.fap.swp391.model.response;

import lombok.Data;

@Data
public class EmailDetail {
    String recipient;

    String subject;

    String fullName;

    String url;

}
