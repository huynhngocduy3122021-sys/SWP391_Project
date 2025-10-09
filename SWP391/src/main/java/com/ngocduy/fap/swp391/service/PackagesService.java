package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Packages;
import com.ngocduy.fap.swp391.model.request.PackagesRequest;
import com.ngocduy.fap.swp391.model.response.PackagesResponse;
import com.ngocduy.fap.swp391.repository.PackagesRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PackagesService {
    private PackagesRepository packagesRepository;

    public List <Packages> getPackages() {
    List<Packages> packages = packagesRepository.findAll();
    return packages;
    }

    public List <Packages> getActivePackages() {
        List<Packages> packages = packagesRepository.findByIsActiveTrue();
        return packages;
    }

    public Packages createPackage(Packages packages) {
        Packages pkg =

        return packagesRepository.save(pkg);
    }
    }


