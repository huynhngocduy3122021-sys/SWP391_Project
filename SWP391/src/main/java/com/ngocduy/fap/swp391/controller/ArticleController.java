package com.ngocduy.fap.swp391.controller;


import com.ngocduy.fap.swp391.entity.Article;
import com.ngocduy.fap.swp391.service.ArticleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @PostMapping
    public ResponseEntity createArticle(@Valid @RequestBody Article article) {
        Article newArticle =articleService.createArticle(article);
        return ResponseEntity.ok(newArticle);
    }


}




