package com.ngocduy.fap.swp391.controller;


import com.ngocduy.fap.swp391.entity.Admin;
import com.ngocduy.fap.swp391.model.request.AdminRequest;
import com.ngocduy.fap.swp391.model.response.AdminResponse;
import com.ngocduy.fap.swp391.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService, ModelMapper modelMapper) {
        this.adminService = adminService;
    }

    // GET all admins
    @GetMapping
    public ResponseEntity getAllAdmins() {
        List<AdminResponse> responses = adminService.findAllAdminResponses();
        return ResponseEntity.ok(responses);
    }

    // GET admin by ID
    @GetMapping("/{id}")
    public ResponseEntity getAdminById(@PathVariable Long id) {
        AdminResponse adminResponse = adminService.findAdminResponseById(id);
        return ResponseEntity.ok(adminResponse);
    }

    // CREATE a new admin
    @PostMapping
    public ResponseEntity createAdmin(@Valid @RequestBody AdminRequest adminRequest) {
        AdminResponse savedAdminResponse = adminService.createAdmin(adminRequest);
        return new ResponseEntity<>(savedAdminResponse, HttpStatus.CREATED);
    }

    // UPDATE an existing admin
    @PutMapping("/{id}")
    public ResponseEntity<AdminResponse> updateAdmin(@PathVariable Long id, @Valid @RequestBody AdminRequest adminRequest) {
        AdminResponse updatedAdminResponse = adminService.updateAdmin(id, adminRequest);
        return ResponseEntity.ok(updatedAdminResponse);
    }

    // DELETE an admin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
