package com.ngocduy.fap.swp391.controller;


import com.ngocduy.fap.swp391.entity.Article.ArticleStatus;
import com.ngocduy.fap.swp391.model.request.ArticleRequest;
import com.ngocduy.fap.swp391.model.response.ArticleResponse;
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

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAllArticles() {
        List<ArticleResponse> articles = articleService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable Long id) {
        ArticleResponse article = articleService.getArticleById(id);
        return ResponseEntity.ok(article);
    }

    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(@Valid @RequestBody ArticleRequest request) {
        ArticleResponse newArticle = articleService.createArticle(request);
        return new ResponseEntity<>(newArticle, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
        ArticleResponse updatedArticle = articleService.updateArticle(id, request);
        return ResponseEntity.ok(updatedArticle);
    }

    @PostMapping("/{articleId}/approve")
    public ResponseEntity<ArticleResponse> approveArticle(@PathVariable Long articleId, @RequestParam("adminId") Long adminId) {
        ArticleResponse approvedArticle = articleService.approveArticle(articleId, adminId);
        return ResponseEntity.ok(approvedArticle);
    }

    @PostMapping("/{articleId}/reject")
    public ResponseEntity<ArticleResponse> rejectArticle(@PathVariable Long articleId, @RequestParam("adminId") Long adminId) {
        ArticleResponse rejectedArticle = articleService.rejectArticle(articleId, adminId);
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




