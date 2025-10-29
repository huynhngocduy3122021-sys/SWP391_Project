package com.ngocduy.fap.swp391.controller;


import com.ngocduy.fap.swp391.entity.Article.ArticleStatus;
import com.ngocduy.fap.swp391.model.request.ArticleRequest;
import com.ngocduy.fap.swp391.model.request.BatteryArticleRequest;
import com.ngocduy.fap.swp391.model.request.CarArticleRequest;
import com.ngocduy.fap.swp391.model.request.MotorArticleRequest;
import com.ngocduy.fap.swp391.model.response.ArticleResponse;
import com.ngocduy.fap.swp391.model.response.BatteryArticleResponse;
import com.ngocduy.fap.swp391.model.response.CarArticleResponse;
import com.ngocduy.fap.swp391.model.response.MotorArticleResponse;
import com.ngocduy.fap.swp391.service.ArticleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/article")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // --- Create Endpoints ---
    @PostMapping("/car")
    public ResponseEntity<CarArticleResponse> createCarArticle(@Valid @RequestBody CarArticleRequest request) {
        CarArticleResponse newArticle = articleService.createCarArticle(request);
        return new ResponseEntity<>(newArticle, HttpStatus.CREATED);
    }

    @PostMapping("/motor")
    public ResponseEntity<MotorArticleResponse> createMotorArticle(@Valid @RequestBody MotorArticleRequest request) {
        MotorArticleResponse newArticle = articleService.createMotorArticle(request);
        return new ResponseEntity<>(newArticle, HttpStatus.CREATED);
    }

    @PostMapping("/battery")
    public ResponseEntity<BatteryArticleResponse> createBatteryArticle(@Valid @RequestBody BatteryArticleRequest request) {
        BatteryArticleResponse newArticle = articleService.createBatteryArticle(request);
        return new ResponseEntity<>(newArticle, HttpStatus.CREATED);
    }

    // --- Update Endpoints ---
    @PutMapping("/car/{id}")
    public ResponseEntity<CarArticleResponse> updateCarArticle(@PathVariable Long id, @Valid @RequestBody CarArticleRequest request) {
        CarArticleResponse updatedArticle = articleService.updateCarArticle(id, request);
        return ResponseEntity.ok(updatedArticle);
    }

    @PutMapping("/motor/{id}")
    public ResponseEntity<MotorArticleResponse> updateMotorArticle(@PathVariable Long id, @Valid @RequestBody MotorArticleRequest request) {
        MotorArticleResponse updatedArticle = articleService.updateMotorArticle(id, request);
        return ResponseEntity.ok(updatedArticle);
    }

    @PutMapping("/battery/{id}")
    public ResponseEntity<BatteryArticleResponse> updateBatteryArticle(@PathVariable Long id, @Valid @RequestBody BatteryArticleRequest request) {
        BatteryArticleResponse updatedArticle = articleService.updateBatteryArticle(id, request);
        return ResponseEntity.ok(updatedArticle);
    }

    // --- Get by ID (Returns a generic ArticleResponse, then client can check articleType) ---
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable Long id) {
        ArticleResponse article = articleService.getArticleById(id);
        return ResponseEntity.ok(article);
    }

    // All other common endpoints remain as is, returning List<ArticleResponse> or single ArticleResponse
    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAllArticles() {
        List<ArticleResponse> articles = articleService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    @PostMapping("/{articleId}/approve")
    public ResponseEntity<ArticleResponse> approveArticle(@PathVariable Long articleId, @RequestParam("memberId") Long memberId) {
        ArticleResponse approvedArticle = articleService.approveArticle(articleId, memberId);
        return ResponseEntity.ok(approvedArticle);
    }

    @PostMapping("/{articleId}/reject")
    public ResponseEntity<ArticleResponse> rejectArticle(@PathVariable Long articleId, @RequestParam("memberId") Long memberId) {
        ArticleResponse rejectedArticle = articleService.rejectArticle(articleId, memberId);
        return ResponseEntity.ok(rejectedArticle);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<ArticleResponse>> getArticlesByMemberId(@PathVariable Long memberId) {
        List<ArticleResponse> articles = articleService.getArticlesByMemberId(memberId);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/status")
    public ResponseEntity<List<ArticleResponse>> getArticlesByStatus(@RequestParam("status") ArticleStatus status) {
        List<ArticleResponse> articles = articleService.getArticlesByStatus(status);
        return ResponseEntity.ok(articles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        boolean deleted = articleService.deleteArticle(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Article deleted successfully");
    }
}

