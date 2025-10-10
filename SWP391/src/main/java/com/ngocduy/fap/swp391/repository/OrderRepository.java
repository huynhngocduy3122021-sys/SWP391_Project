package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByIsDeletedFalse();
    Optional<Order> findByOrderIdAndIsDeletedFalse(Long orderId);
    List<Order> findByMemberMemberIdAndIsDeletedFalse(Long memberId);
    List<Order> findByStatusAndIsDeletedFalse(String status);
}
