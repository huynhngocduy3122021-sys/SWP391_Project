package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.CarArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CarArticleRepository extends JpaRepository<CarArticle, Long> {


    Optional<CarArticle> findByArticle_ArticleId(Long articleId);
    List<CarArticle> findByDeletedFalse();
    Optional<CarArticle> findByIdAndDeletedFalse(Long id);

}
