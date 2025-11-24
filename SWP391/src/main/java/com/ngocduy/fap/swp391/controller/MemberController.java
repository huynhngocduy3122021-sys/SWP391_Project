 package com.ngocduy.fap.swp391.controller;


import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.model.request.ForgotPasswordRequest;
import com.ngocduy.fap.swp391.model.request.LoginRequest;
import com.ngocduy.fap.swp391.model.request.ResetPasswordRequest;
import com.ngocduy.fap.swp391.model.response.MemberResponse;
import com.ngocduy.fap.swp391.service.MemberService;
import com.ngocduy.fap.swp391.model.request.MemberRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
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



    @GetMapping("/allmember")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Member>> getAllMember() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }


    //test member hien dang login
    @GetMapping("/current")
    public ResponseEntity<MemberResponse> getCurrentMember() {
        return ResponseEntity.ok(memberService.getCurrentMember());
    }

    // GET member by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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

    // Forgot Password - Send reset link via email
    @PostMapping("/forgot-password")
    @SecurityRequirement(name = "")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        memberService.forgotPassword(request.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("message", "If the email exists, a password reset link has been sent.");
        return ResponseEntity.ok(response);
    }

    // Reset Password - Update password with token
    @PostMapping("/reset-password")
    @SecurityRequirement(name = "")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        memberService.resetPassword(request.getToken(), request.getNewPassword());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password has been reset successfully.");
        return ResponseEntity.ok(response);
    }
}

