package com.ngocduy.fap.swp391.model.response;


import com.ngocduy.fap.swp391.enums.MemberStatus;
import com.ngocduy.fap.swp391.enums.Role;
import lombok.Data;


import java.time.LocalDate;
@Data
public class MemberResponse {


    private long memberId;
    private String name;
    private String address;
    private LocalDate yearOfBirth;
    private String phone;
    private String email;
    private MemberStatus status;
    private Role role;
    private String sex;
    private String token;

}
