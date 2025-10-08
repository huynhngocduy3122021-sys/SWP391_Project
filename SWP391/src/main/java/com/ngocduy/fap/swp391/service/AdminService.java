package com.ngocduy.fap.swp391.service;


import com.ngocduy.fap.swp391.entity.Admin;
import com.ngocduy.fap.swp391.model.request.AdminRequest;
import com.ngocduy.fap.swp391.model.response.AdminResponse;
import com.ngocduy.fap.swp391.repository.AdminRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper; // Inject ModelMapper here

    @Autowired
    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public List findAllAdminResponses() {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream()
                .map(admin -> modelMapper.map(admin, AdminResponse.class))
                .collect(Collectors.toList());
    }

    public AdminResponse findAdminResponseById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with id: " + id));
        return modelMapper.map(admin, AdminResponse.class);
    }

    public AdminResponse createAdmin(AdminRequest adminRequest) {
        if (adminRepository.existsByEmail(adminRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin with this email already exists.");
        }
        if (adminRepository.existsByPhone(adminRequest.getPhone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin with this phone number already exists.");
        }

        Admin admin = modelMapper.map(adminRequest, Admin.class);
        // Hash password before saving
        if(admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        Admin savedAdmin = adminRepository.save(admin);
        return modelMapper.map(savedAdmin, AdminResponse.class);
    }

    public AdminResponse updateAdmin(Long id, AdminRequest adminRequest) {
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with id: " + id));

        // Check for email/phone conflicts with other admins
        if (!existingAdmin.getEmail().equalsIgnoreCase(adminRequest.getEmail()) && adminRepository.existsByEmail(adminRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use by another admin.");
        }
        if (!existingAdmin.getPhone().equals(adminRequest.getPhone()) && adminRepository.existsByPhone(adminRequest.getPhone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already in use by another admin.");
        }

        // Preserve the existing password if not provided in the request
        String currentPasswordHash = existingAdmin.getPassword();
        modelMapper.map(adminRequest, existingAdmin); // Map updated fields to existing entity

        // Only update password if a new one is provided in the request
        if (adminRequest.getPassword() != null && !adminRequest.getPassword().isEmpty()) {
            existingAdmin.setPassword(passwordEncoder.encode(adminRequest.getPassword()));
        } else {
            existingAdmin.setPassword(currentPasswordHash); // Retain old password if not provided
        }

        Admin updatedAdmin = adminRepository.save(existingAdmin);
        return modelMapper.map(updatedAdmin, AdminResponse.class);
    }

    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with id: " + id);
        }
        adminRepository.deleteById(id);
    }

    // Keep existing helper methods for internal service use
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }

    public boolean existsByPhone(String phone) {
        return adminRepository.existsByPhone(phone);
    }
}
