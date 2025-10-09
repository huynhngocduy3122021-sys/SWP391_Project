package com.ngocduy.fap.swp391.controller;

import com.ngocduy.fap.swp391.service.PackageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/package")
public class PackageController {

    @Autowired
    PackageService packageService;
}
