package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Packages;
import com.ngocduy.fap.swp391.exception.exceptions.NotFoundException;
import com.ngocduy.fap.swp391.model.request.PackagesRequest;
import com.ngocduy.fap.swp391.model.response.PackagesResponse;
import com.ngocduy.fap.swp391.repository.PackagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackagesService {
    
    @Autowired
    private PackagesRepository packagesRepository;

    // Get all packages
    public List<PackagesResponse> getAllPackages() {
        return packagesRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get active packages
    public List<PackagesResponse> getActivePackages() {
        return packagesRepository.findByIsActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get package by ID
    public PackagesResponse getPackageById(Long id) {
        Packages pkg = packagesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Package not found with id: " + id));
        return convertToResponse(pkg);
    }

    // Create package
    public PackagesResponse createPackage(PackagesRequest request) {
        // Validation
        if (request.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (request.getNumberOfPost() <= 0) {
            throw new IllegalArgumentException("Number of posts must be greater than 0");
        }
        if (request.getDurationDays() == null || request.getDurationDays() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        Packages pkg = new Packages();
        pkg.setName(request.getName());
        pkg.setNumberOfPost(request.getNumberOfPost());
        pkg.setDescription(request.getDescription());
        pkg.setPrice(request.getPrice());
        pkg.setDurationDays(request.getDurationDays());
        pkg.setActive(true);

        Packages savedPackage = packagesRepository.save(pkg);
        return convertToResponse(savedPackage);
    }

    // Update package
    public PackagesResponse updatePackage(Long id, PackagesRequest request) {
        Packages pkg = packagesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Package not found with id: " + id));

        // Validation
        if (request.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (request.getNumberOfPost() <= 0) {
            throw new IllegalArgumentException("Number of posts must be greater than 0");
        }
        if (request.getDurationDays() != null && request.getDurationDays() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }

        pkg.setName(request.getName());
        pkg.setNumberOfPost(request.getNumberOfPost());
        pkg.setDescription(request.getDescription());
        pkg.setPrice(request.getPrice());
        if (request.getDurationDays() != null) {
            pkg.setDurationDays(request.getDurationDays());
        }

        Packages updatedPackage = packagesRepository.save(pkg);
        return convertToResponse(updatedPackage);
    }

    // Delete package (soft delete)
    public void deletePackage(Long id) {
        Packages pkg = packagesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Package not found with id: " + id));
        pkg.setActive(false);
        packagesRepository.save(pkg);
    }

    // Convert entity to response
    private PackagesResponse convertToResponse(Packages pkg) {
        PackagesResponse response = new PackagesResponse();
        response.setPackageId(pkg.getPackageId());
        response.setName(pkg.getName());
        response.setNumberOfPost(pkg.getNumberOfPost());
        response.setDescription(pkg.getDescription());
        response.setPrice(pkg.getPrice());
        response.setDurationDays(pkg.getDurationDays());
        response.setActive(pkg.isActive());
        return response;
    }
}


