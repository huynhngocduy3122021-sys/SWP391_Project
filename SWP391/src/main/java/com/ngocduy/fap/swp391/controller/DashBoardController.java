package com.ngocduy.fap.swp391.controller;

import com.ngocduy.fap.swp391.service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/dashboard")
public class DashBoardController {
    @Autowired
    DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity getDashboardStats() {
        Map<String, Object> stats = dashboardService.getDashBoardStats();
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/monthly-revenue")
    public ResponseEntity getMonthlyRevenue() {
        Map<String, Object> monthlyRevenue = dashboardService.getMonthlyRevenue();
        return ResponseEntity.ok(monthlyRevenue);
    }

    @GetMapping("yearly-revenue")
    public ResponseEntity getYearlyRevenue() {
        Map<String, Object> yearlyRevenue = dashboardService.getYearlyRevenue();
        return ResponseEntity.ok(yearlyRevenue);
    }

}
