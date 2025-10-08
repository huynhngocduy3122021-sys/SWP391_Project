package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
