package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByIsDeletedFalse();
    Optional<Payment> findByPayIdAndIsDeletedFalse(Long payId);
    List<Payment> findByStatusAndIsDeletedFalse(String status);
}
