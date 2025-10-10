package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Payment;
import com.ngocduy.fap.swp391.exception.exceptions.NotFoundException;
import com.ngocduy.fap.swp391.model.request.PaymentRequest;
import com.ngocduy.fap.swp391.model.response.PaymentResponse;
import com.ngocduy.fap.swp391.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Get all payments (excluding deleted)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findByIsDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get payment by ID (excluding deleted)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findByPayIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Payment not found with id: " + id));
        return convertToResponse(payment);
    }

    // Get payments by status (excluding deleted)
    public List<PaymentResponse> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatusAndIsDeletedFalse(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Create payment
    public PaymentResponse createPayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setMethod(request.getMethod());
        payment.setTransactionCode(request.getTransactionCode());
        payment.setAmount(request.getAmount());
        payment.setStatus(request.getStatus());
        payment.setPaymentDate(LocalDateTime.now());
        
        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
    }

    // Update payment
    public PaymentResponse updatePayment(Long id, PaymentRequest request) {
        Payment payment = paymentRepository.findByPayIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Payment not found with id: " + id));
        
        payment.setMethod(request.getMethod());
        payment.setTransactionCode(request.getTransactionCode());
        payment.setAmount(request.getAmount());
        payment.setStatus(request.getStatus());
        
        Payment updatedPayment = paymentRepository.save(payment);
        return convertToResponse(updatedPayment);
    }

    // Delete payment (soft delete)
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findByPayIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Payment not found with id: " + id));
        payment.setDeleted(true);
        paymentRepository.save(payment);
    }

    // Convert entity to response
    private PaymentResponse convertToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPayId(payment.getPayId());
        response.setMethod(payment.getMethod());
        response.setTransactionCode(payment.getTransactionCode());
        response.setAmount(payment.getAmount());
        response.setPaymentDate(payment.getPaymentDate());
        response.setStatus(payment.getStatus());
        return response;
    }
}
