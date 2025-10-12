package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.MotorArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MotorArticleRepository extends JpaRepository<MotorArticle,Long> {
}
