package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Subscription;
import com.ngocduy.fap.swp391.entity.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
}
