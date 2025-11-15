package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Order;
import com.ngocduy.fap.swp391.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByIsDeletedFalse();
    Optional<Order> findByOrderIdAndIsDeletedFalse(Long orderId);
    List<Order> findByMemberMemberIdAndIsDeletedFalse(Long memberId);
    List<Order> findByStatusAndIsDeletedFalse(OrderStatus status);

    @Query("SELECT YEAR(o.date) as year, MONTH(o.date) as month, SUM(o.totalAmount) " +
            "FROM Order o WHERE o.isDeleted = false AND o.status = 'COMPLETED' " +
            "GROUP BY YEAR(o.date), MONTH(o.date) " +
            "ORDER BY YEAR(o.date), MONTH(o.date)")
    List<Object[]> calculateMonthlyRevenue();

    @Query("SELECT YEAR(o.date) as year, SUM(o.totalAmount) " +
            "FROM Order o WHERE o.isDeleted = false AND o.status = 'COMPLETED' " +
            "GROUP BY YEAR(o.date)" +
            "ORDER BY YEAR(o.date)")
    List<Object[]> calculateYearlyRevenue();

    @Query("SELECT SUM(o.totalAmount) " +
            "FROM Order o WHERE o.isDeleted = false AND o.status = 'PAID'")
    Double calculateTotalRevenuePaid();
}
