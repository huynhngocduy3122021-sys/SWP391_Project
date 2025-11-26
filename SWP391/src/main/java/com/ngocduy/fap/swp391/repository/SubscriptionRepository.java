package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Subscription;
import com.ngocduy.fap.swp391.entity.SubscriptionId;
import com.ngocduy.fap.swp391.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    List<Subscription> findByIsDeletedFalse();
    Optional<Subscription> findByIdAndIsDeletedFalse(SubscriptionId id);
    List<Subscription> findByIdMemberIdAndIsDeletedFalse(Long memberId);
    List<Subscription> findByStatusAndIsDeletedFalse(String status);

    // Find subscriptions that are expired (endDate < now and status = ACTIVE)
    @Query("SELECT s FROM Subscription s WHERE s.status = :status AND s.endDate < :now AND s.isDeleted = false")
    List<Subscription> findExpiredSubscriptions(@Param("status") String status, @Param("now") LocalDateTime now);

    @Query("""
            SELECT s FROM Subscription s
            WHERE s.id.memberId = :memberId
              AND s.status = 'ACTIVE'
              AND s.isDeleted = false
              AND (s.endDate IS NULL OR s.endDate > :now)
              AND COALESCE(s.remainingPosts, 0) > 0
            ORDER BY s.endDate ASC
            """)
    Optional<Subscription> findFirstActiveSubscriptionWithRemainingPosts(@Param("memberId") Long memberId,
                                                                         @Param("now") LocalDateTime now);

    @Query("SELECT s.pkg.name, COUNT(s) " +
            "FROM Subscription s " +
            "WHERE s.isDeleted = false AND s.status = 'ACTIVE' " +
            "GROUP BY s.pkg.name")
    List<Object[]> countActiveSubscriptionsByPackage();

    @Query("SELECT MONTH(s.startDate) as month, s.pkg.name, COUNT(s) " +
            "FROM Subscription s " +
            "WHERE s.isDeleted = false AND s.status = 'ACTIVE' AND YEAR(s.startDate) = :year " +
            "GROUP BY MONTH(s.startDate), s.pkg.name " +
            "ORDER BY MONTH(s.startDate)")
    List<Object[]> countMonthlyActiveSubscriptionsByPackage(@Param("year") int year);
}
