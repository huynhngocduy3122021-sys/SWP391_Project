package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import static com.ngocduy.fap.swp391.entity.Article.ArticleStatus;

import java.util.List;


public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByStatus(ArticleStatus status);

    List<Article> findByMember_MemberId(Long memberId);
}
