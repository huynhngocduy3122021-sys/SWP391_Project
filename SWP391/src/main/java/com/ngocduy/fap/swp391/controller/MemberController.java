package com.ngocduy.fap.swp391.controller;


import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.model.request.LoginRequest;
import com.ngocduy.fap.swp391.model.response.MemberResponse;
import com.ngocduy.fap.swp391.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MemberController {

    // điều hướng (controller) => xử lý logic (service) => lưu DB (repository) (JPA)
    @Autowired
    MemberService memberService;



    @PostMapping("/api/register")
    public ResponseEntity register(@Valid @RequestBody Member member) {
        //nhan yeu cau tu FE
        // => day qua authenticationservice
        Member newMember = memberService.register(member);
        return ResponseEntity.ok(newMember);
    }

    //login*
    @PostMapping("/api/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest login) {
        // đưa qua memberService xử lí
        MemberResponse memberLogin = memberService.login(login);
        return ResponseEntity.ok(memberLogin);
    }



    //test get all member
    @GetMapping("api/member")
    public ResponseEntity getAllMember() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }



}
