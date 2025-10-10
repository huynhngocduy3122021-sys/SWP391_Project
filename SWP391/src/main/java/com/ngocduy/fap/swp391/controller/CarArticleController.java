package com.ngocduy.fap.swp391.controller;


import com.ngocduy.fap.swp391.exception.exceptions.ResourceNotFoundException;
import com.ngocduy.fap.swp391.model.request.CarArticleRequest;
import com.ngocduy.fap.swp391.model.response.CarArticleResponse;
import com.ngocduy.fap.swp391.service.CarArticleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/cars-Article")
public class CarArticleController {

    private final CarArticleService carArticleService;

    public CarArticleController(CarArticleService carArticleService) {
        this.carArticleService = carArticleService;
    }

    @GetMapping
    public ResponseEntity<List<CarArticleResponse>> getAllActiveCarArticles() {
        List<CarArticleResponse> carArticles = carArticleService.getAllActiveCarArticles();
        return ResponseEntity.ok(carArticles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarArticleResponse> getCarArticleById(@PathVariable Long id) {
        return carArticleService.getCarArticleById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Active CarArticle not found with ID: " + id));
    }

    @PostMapping
    public ResponseEntity<CarArticleResponse> createCarArticle(@Valid @RequestBody CarArticleRequest carArticleRequest) {
        CarArticleResponse createdCarArticleResponse = carArticleService.createCarArticle(carArticleRequest);
        return new ResponseEntity<>(createdCarArticleResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarArticleResponse> updateCarArticle(@PathVariable Long id, @Valid @RequestBody CarArticleRequest carArticleRequest) {
        CarArticleResponse updatedCarArticleResponse = carArticleService.updateCarArticle(id, carArticleRequest);
        return ResponseEntity.ok(updatedCarArticleResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCarArticle(@PathVariable Long id) {
        carArticleService.softDeleteCarArticle(id);
        return ResponseEntity.noContent().build();
    }
}
