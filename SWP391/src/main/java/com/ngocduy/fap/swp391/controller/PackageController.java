package com.ngocduy.fap.swp391.controller;

import com.ngocduy.fap.swp391.model.request.PackagesRequest;
import com.ngocduy.fap.swp391.model.response.PackagesResponse;
import com.ngocduy.fap.swp391.service.PackagesService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/package")
public class PackageController {

    @Autowired
    private PackagesService packagesService;

    // Get all packages
    @GetMapping
    public ResponseEntity<List<PackagesResponse>> getAllPackages() {
        List<PackagesResponse> packages = packagesService.getAllPackages();
        return ResponseEntity.ok(packages);
    }

    // Get active packages
    @GetMapping("/active")
    public ResponseEntity<List<PackagesResponse>> getActivePackages() {
        List<PackagesResponse> packages = packagesService.getActivePackages();
        return ResponseEntity.ok(packages);
    }

    // Get package by ID
    @GetMapping("/{id}")
    public ResponseEntity<PackagesResponse> getPackageById(@PathVariable Long id) {
        PackagesResponse pkg = packagesService.getPackageById(id);
        return ResponseEntity.ok(pkg);
    }

    // Create package
    @PostMapping
    public ResponseEntity<PackagesResponse> createPackage(@RequestBody PackagesRequest request) {
        PackagesResponse pkg = packagesService.createPackage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pkg);
    }

    // Update package
    @PutMapping("/update/{id}")
    public ResponseEntity<PackagesResponse> updatePackage(
            @PathVariable Long id,
            @RequestBody PackagesRequest request) {
        PackagesResponse pkg = packagesService.updatePackage(id, request);
        return ResponseEntity.ok(pkg);
    }

    // Delete package (soft delete)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        packagesService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}
