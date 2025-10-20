package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Subscription;
import com.ngocduy.fap.swp391.entity.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    List<Subscription> findByIsDeletedFalse();
    Optional<Subscription> findByIdAndIsDeletedFalse(SubscriptionId id);
    List<Subscription> findByIdMemberIdAndIsDeletedFalse(Long memberId);
    List<Subscription> findByStatusAndIsDeletedFalse(String status);
}
