package com.ngocduy.fap.swp391.model.response;


import lombok.Data;


import java.time.LocalDate;
@Data
public class MemberResponse {


    long memberId;
    String name;
    String address;
    LocalDate yearOfBirth;
    String phone;
    String email;
    String status;
    String sex;
    String token;

}
