 package com.ngocduy.fap.swp391.controller;


import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.model.request.LoginRequest;
import com.ngocduy.fap.swp391.model.response.MemberResponse;
import com.ngocduy.fap.swp391.service.MemberService;
import com.ngocduy.fap.swp391.model.request.MemberRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/members")
public class MemberController {

    // điều hướng (controller) => xử lý logic (service) => lưu DB (repository) (JPA)
    @Autowired
    MemberService memberService;



    @PostMapping("/register")
    public ResponseEntity<Member> register(@Valid @RequestBody Member member) {
        //nhan yeu cau tu FE
        // => day qua authenticationservice
        Member newMember = memberService.register(member);
        return ResponseEntity.ok(newMember);
    }

    //login*
    @PostMapping("/login")
    public ResponseEntity<MemberResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // đưa qua memberService xử lí
        MemberResponse member = memberService.login(loginRequest);
        return ResponseEntity.ok(member);
    }


    /*
    //test get all member
    @GetMapping("/allmember")
    public ResponseEntity<List<Member>> getAllMember() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }
     */

    //test member hien dang login
    @GetMapping("/current")
    public ResponseEntity<Member> getCurrentMember() {
        return ResponseEntity.ok(memberService.getCurrentMember());
    }

    // GET member by ID
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Member member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    //  UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        Member updated = memberService.updateMember(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        MemberResponse response = new MemberResponse();
        response.setMemberId(updated.getMemberId());
        response.setName(updated.getName());
        response.setEmail(updated.getEmail());
        response.setPhone(updated.getPhone());
        response.setAddress(updated.getAddress());
        response.setStatus(updated.getStatus());
        response.setYearOfBirth(updated.getYearOfBirth());
        response.setSex(updated.getSex());
        return ResponseEntity.ok(response);
    }

    //  DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable Long id) {
        boolean deleted = memberService.deleteMember(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Member deleted successfully");
    }
}

