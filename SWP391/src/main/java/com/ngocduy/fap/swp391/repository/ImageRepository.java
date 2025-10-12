package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Article;
import com.ngocduy.fap.swp391.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByArticle_ArticleId(Long articleId);
    
    java.util.Optional<Image> findByArticle_ArticleIdAndIsMainTrue(Long articleId);
}
