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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/members")
public class MemberController {

    // điều hướng (controller) => xử lý logic (service) => lưu DB (repository) (JPA)
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @PostMapping("/register")
    public ResponseEntity<MemberResponse> register(@Valid @RequestBody MemberRequest member) {
        //nhan yeu cau tu FE
        // => day qua authenticationservice
        MemberResponse newMember = memberService.register(member);
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
    public ResponseEntity<MemberResponse> getCurrentMember() {
        return ResponseEntity.ok(memberService.getCurrentMember());
    }

    // GET member by ID
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long id) {
        MemberResponse member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    //  UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.updateMember(id, request);
        return ResponseEntity.ok(response);
    }

    //  DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MemberResponse>> getAllActiveMembers() {
        List<MemberResponse> response = memberService.getActiveMembers();
        return ResponseEntity.ok(response);
    }
}

