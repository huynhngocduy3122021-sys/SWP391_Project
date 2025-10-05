package com.ngocduy.fap.swp391.model.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MemberRequest {
    private String name;
    private String address;
    private LocalDate yearOfBirth;
    private String phone;
    private String email;
    private String status;
    private String sex;
    private String password;

}