package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.entity.Order;
import com.ngocduy.fap.swp391.entity.Packages;
import com.ngocduy.fap.swp391.enums.OrderStatus;
import com.ngocduy.fap.swp391.enums.PaymentStatus;
import com.ngocduy.fap.swp391.exception.exceptions.NotFoundException;
import com.ngocduy.fap.swp391.model.request.OrderRequest;
import com.ngocduy.fap.swp391.model.response.OrderResponse;
import com.ngocduy.fap.swp391.repository.MemberRepository;
import com.ngocduy.fap.swp391.repository.OrderRepository;
import com.ngocduy.fap.swp391.repository.PackagesRepository;
import com.ngocduy.fap.swp391.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PackagesRepository packagesRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    // Get all orders (excluding deleted)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findByIsDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get order by ID (excluding deleted)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
        return convertToResponse(order);
    }

    // Get orders by member ID (excluding deleted)
    public List<OrderResponse> getOrdersByMemberId(Long memberId) {
        return orderRepository.findByMemberMemberIdAndIsDeletedFalse(memberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get orders by status (excluding deleted)
    public List<OrderResponse> getOrdersByStatus(String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        return orderRepository.findByStatusAndIsDeletedFalse(orderStatus).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Create order
    public OrderResponse createOrder(OrderRequest request) {
        // 1. Lấy thông tin package và member
        Packages pkg = packagesRepository.findById(request.getPackageId())
                .orElseThrow(() -> new NotFoundException("Package not found with id: " + request.getPackageId()));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new NotFoundException("Member not found with id: " + request.getMemberId()));

        // 2. Tạo đơn hàng với trạng thái PENDING
        Order order = new Order();
        order.setMember(member);
        order.setPkg(pkg);
        order.setTotalAmount(pkg.getPrice());
        order.setDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        // 3. Lưu đơn hàng (không tạo Payment ở đây)
        Order savedOrder = orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    // Update order
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));

        // Update relationships if changed
        if (request.getMemberId() != null) {
            Member member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new NotFoundException("Member not found with id: " + request.getMemberId()));
            order.setMember(member);
        }

        if (request.getPackageId() != null) {
            Packages pkg = packagesRepository.findById(request.getPackageId())
                    .orElseThrow(() -> new NotFoundException("Package not found with id: " + request.getPackageId()));
            order.setPkg(pkg);
            order.setTotalAmount(pkg.getPrice());
        }

        Order updatedOrder = orderRepository.save(order);
        return convertToResponse(updatedOrder);
    }

    // Delete order (soft delete)
    public void deleteOrder(Long id) {
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
        order.setDeleted(true);
        orderRepository.save(order);
    }

    // Confirm order (change status to CONFIRMED)
    public OrderResponse confirmOrder(Long id) {
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));

        // Only confirm if payment is PAID
        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("Cannot confirm order. Payment status must be PAID");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        Order updatedOrder = orderRepository.save(order);
        return convertToResponse(updatedOrder);
    }

    // Complete order (change status to COMPLETED)
    public OrderResponse completeOrder(Long id) {
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));

        // Only complete if order is CONFIRMED
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot complete order. Order must be CONFIRMED first");
        }

        order.setStatus(OrderStatus.COMPLETED);
        Order updatedOrder = orderRepository.save(order);
        return convertToResponse(updatedOrder);
    }

    // Cancel order
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));

        // Cannot cancel if already COMPLETED
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed order");
        }

        order.setStatus(OrderStatus.CANCELLED);

        // If payment was made, mark for refund
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        Order updatedOrder = orderRepository.save(order);
        return convertToResponse(updatedOrder);
    }

    // Update payment status after payment is processed
    public OrderResponse updatePaymentStatus(Long id, String paymentStatusStr) {
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));

        PaymentStatus paymentStatus = PaymentStatus.valueOf(paymentStatusStr);
        order.setPaymentStatus(paymentStatus);

        // Auto-confirm order if payment is successful
        if (paymentStatus == PaymentStatus.PAID && order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CONFIRMED);
        }

        Order updatedOrder = orderRepository.save(order);
        return convertToResponse(updatedOrder);
    }

    // Convert entity to response
    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setTotalAmount(order.getTotalAmount());
        response.setDate(order.getDate());
        response.setStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        
        if (order.getMember() != null) {
            response.setMemberId(order.getMember().getMemberId());
            response.setMemberName(order.getMember().getName());
        }
        
        if (order.getPkg() != null) {
            response.setPackageId(order.getPkg().getPackageId());
            response.setPackageName(order.getPkg().getName());
        }
        
        // Lấy payment thành công cuối cùng nếu có
        if (order.getPayments() != null && !order.getPayments().isEmpty()) {
            order.getPayments().stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()) || "COMPLETED".equals(p.getStatus()))
                .findFirst()
                .ifPresent(p -> response.setPayId(p.getPayId()));
        }
        
        return response;
    }
}
