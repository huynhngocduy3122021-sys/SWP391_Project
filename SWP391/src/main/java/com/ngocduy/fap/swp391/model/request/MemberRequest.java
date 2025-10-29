package com.ngocduy.fap.swp391.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;

@Data
public class MemberRequest {
    private String name;
    private String address;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate yearOfBirth;
    private String phone;
    private String email;
    private String role;
    private String sex;
    private String password;

}