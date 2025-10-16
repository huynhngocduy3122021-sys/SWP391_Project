package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.entity.Order;
import com.ngocduy.fap.swp391.entity.Packages;
import com.ngocduy.fap.swp391.entity.Payment;
import com.ngocduy.fap.swp391.exception.exceptions.NotFoundException;
import com.ngocduy.fap.swp391.model.request.OrderRequest;
import com.ngocduy.fap.swp391.model.response.OrderResponse;
import com.ngocduy.fap.swp391.repository.MemberRepository;
import com.ngocduy.fap.swp391.repository.OrderRepository;
import com.ngocduy.fap.swp391.repository.PackagesRepository;
import com.ngocduy.fap.swp391.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return orderRepository.findByStatusAndIsDeletedFalse(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Create order
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setTotalAmount(request.getTotalAmount());
        order.setDate(request.getDate());
        order.setStatus(request.getStatus());
        order.setPaymentStatus(request.getPaymentStatus());

        // Set relationships
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new NotFoundException("Member not found with id: " + request.getMemberId()));
        order.setMember(member);

        Packages pkg = packagesRepository.findById(request.getPackageId())
                .orElseThrow(() -> new NotFoundException("Package not found with id: " + request.getPackageId()));
        order.setPkg(pkg);

        Payment payment = paymentRepository.findById(request.getPayId())
                .orElseThrow(() -> new NotFoundException("Payment not found with id: " + request.getPayId()));
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);
        return convertToResponse(savedOrder);
    }

    // Update order
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));

        order.setTotalAmount(request.getTotalAmount());
        order.setDate(request.getDate());
        order.setStatus(request.getStatus());
        order.setPaymentStatus(request.getPaymentStatus());

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
        }

        if (request.getPayId() != null) {
            Payment payment = paymentRepository.findById(request.getPayId())
                    .orElseThrow(() -> new NotFoundException("Payment not found with id: " + request.getPayId()));
            order.setPayment(payment);
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
        
        if (order.getPayment() != null) {
            response.setPayId(order.getPayment().getPayId());
        }
        
        return response;
    }
}
