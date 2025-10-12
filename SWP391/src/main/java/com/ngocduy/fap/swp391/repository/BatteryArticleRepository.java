package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.BatteryArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatteryArticleRepository extends JpaRepository<BatteryArticle,Long> {
}
