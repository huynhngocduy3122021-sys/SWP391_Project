package com.ngocduy.fap.swp391.controller;

import com.ngocduy.fap.swp391.entity.Order;
import com.ngocduy.fap.swp391.entity.Payment;
import com.ngocduy.fap.swp391.entity.Subscription;
import com.ngocduy.fap.swp391.entity.SubscriptionId;
import com.ngocduy.fap.swp391.enums.OrderStatus;
import com.ngocduy.fap.swp391.enums.PaymentStatus;
import com.ngocduy.fap.swp391.enums.SubscriptionStatus;
import com.ngocduy.fap.swp391.exception.exceptions.NotFoundException;
import com.ngocduy.fap.swp391.model.request.PaymentRequest;
import com.ngocduy.fap.swp391.model.response.PaymentResponse;
import com.ngocduy.fap.swp391.repository.OrderRepository;
import com.ngocduy.fap.swp391.repository.PaymentRepository;
import com.ngocduy.fap.swp391.repository.SubscriptionRepository;
import com.ngocduy.fap.swp391.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // Get all payments
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    // Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    // Get payments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable String status) {
        List<PaymentResponse> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    // Create payment
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        PaymentResponse payment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    // Update payment status
    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        PaymentResponse payment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(payment);
    }

    // Delete payment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    // Process payment (mark as COMPLETED)
    @PatchMapping("/{id}/process")
    public ResponseEntity<PaymentResponse> processPayment(@PathVariable Long id) {
        PaymentResponse payment = paymentService.processPayment(id);
        return ResponseEntity.ok(payment);
    }

    // Mark payment as FAILED
    @PatchMapping("/{id}/fail")
    public ResponseEntity<PaymentResponse> failPayment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        PaymentResponse payment = paymentService.failPayment(id, reason);
        return ResponseEntity.ok(payment);
    }

    // Refund payment
    @PatchMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long id) {
        PaymentResponse payment = paymentService.refundPayment(id);
        return ResponseEntity.ok(payment);
    }


    // Create VNPAY payment URL for existing order
    @PostMapping("/vnpay/create-url")
    public ResponseEntity<String> createPaymentURL(@RequestParam Long orderId) throws Exception {
        String paymentURL = paymentService.createPaymentURL(orderId);
        return ResponseEntity.ok(paymentURL);
    }

    // Handle VNPAY return callback - Success
    @GetMapping("/vnpay/return/success/{orderId}")
    public ResponseEntity<String> handleVnpaySuccess(
            @PathVariable Long orderId,
            @RequestParam Map<String, String> params) {
        try {
            String vnpResponseCode = params.get("vnp_ResponseCode");
            String vnpTxnRef = params.get("vnp_TxnRef");
            String vnpAmount = params.get("vnp_Amount");
            String vnpBankCode = params.get("vnp_BankCode");
            String vnpTransactionNo = params.get("vnp_TransactionNo");
            String vnpPayDate = params.get("vnp_PayDate");
            
            if ("00".equals(vnpResponseCode)) {
                // 1. Tìm Payment theo vnpTxnRef
                Payment payment = paymentRepository.findByVnpTxnRef(vnpTxnRef)
                        .orElseThrow(() -> new NotFoundException("Payment not found with txnRef: " + vnpTxnRef));
                
                // 2. Cập nhật Payment
                payment.setStatus("COMPLETED");
                payment.setVnpTransactionNo(vnpTransactionNo);
                payment.setVnpBankCode(vnpBankCode);
                payment.setVnpPayDate(vnpPayDate);
                payment.setVnpResponseCode(vnpResponseCode);
                paymentRepository.save(payment);
                
                // 3. Cập nhật Order
                Order order = payment.getOrder();
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
                
                // 4. Tạo Subscription
                SubscriptionId subscriptionId = new SubscriptionId(
                    order.getMember().getMemberId(),
                    order.getPkg().getPackageId()
                );
                
                Subscription subscription = new Subscription();
                subscription.setId(subscriptionId);
                subscription.setMember(order.getMember());
                subscription.setPkg(order.getPkg());
                subscription.setStartDate(java.time.LocalDateTime.now());
                subscription.setEndDate(java.time.LocalDateTime.now().plusDays(order.getPkg().getDurationDays()));
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscription.setRemainingPosts(order.getPkg().getNumberOfPost());
                subscriptionRepository.save(subscription);
                
                return ResponseEntity.ok(
                    "Payment successful!\n" +
                    "Order ID: " + orderId + "\n" +
                    "Transaction: " + vnpTransactionNo + "\n" +
                    "Amount: " + vnpAmount + " VND"
                );
            } else {
                // Thanh toán thất bại - cập nhật Payment status
                Payment payment = paymentRepository.findByVnpTxnRef(vnpTxnRef)
                        .orElseThrow(() -> new NotFoundException("Payment not found"));
                payment.setStatus("FAILED");
                payment.setVnpResponseCode(vnpResponseCode);
                paymentRepository.save(payment);
                
                return ResponseEntity.ok("Payment failed with code: " + vnpResponseCode);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
    
    // Handle VNPAY return callback - General (for backward compatibility)
    @GetMapping("/vnpay/return/vnp")
    public ResponseEntity<Map<String, Object>> handleVnpayReturn(@RequestParam Map<String, String> params) {
        try {
            String vnpResponseCode = params.get("vnp_ResponseCode");
            String vnpTxnRef = params.get("vnp_TxnRef");
            String vnpAmount = params.get("vnp_Amount");
            
            Map<String, Object> result = new HashMap<>();
            
            if ("00".equals(vnpResponseCode)) {
                result.put("success", true);
                result.put("message", "Payment successful");
                result.put("transactionRef", vnpTxnRef);
                result.put("amount", vnpAmount);
            } else {
                result.put("success", false);
                result.put("message", "Payment failed with code: " + vnpResponseCode);
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
}
