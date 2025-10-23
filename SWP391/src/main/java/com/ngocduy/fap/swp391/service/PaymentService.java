package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Order;
import com.ngocduy.fap.swp391.entity.Payment;
import com.ngocduy.fap.swp391.exception.exceptions.NotFoundException;
import com.ngocduy.fap.swp391.model.request.OrderRequest;
import com.ngocduy.fap.swp391.model.request.PaymentRequest;
import com.ngocduy.fap.swp391.model.response.OrderResponse;
import com.ngocduy.fap.swp391.model.response.PaymentResponse;
import com.ngocduy.fap.swp391.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    OrderService orderService;
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
        // status sẽ dùng giá trị mặc định "PENDING" nếu không truyền
        if (request.getStatus() != null) {
            payment.setStatus(request.getStatus());
        }
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

    // Process payment - mark as COMPLETED
    public PaymentResponse processPayment(Long id) {
        Payment payment = paymentRepository.findByPayIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Payment not found with id: " + id));

        if (!"PENDING".equals(payment.getStatus())) {
            throw new IllegalStateException("Can only process PENDING payments");
        }

        payment.setStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        return convertToResponse(updatedPayment);
    }

    // Mark payment as FAILED
    public PaymentResponse failPayment(Long id, String reason) {
        Payment payment = paymentRepository.findByPayIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Payment not found with id: " + id));

        payment.setStatus("FAILED");

        Payment updatedPayment = paymentRepository.save(payment);
        return convertToResponse(updatedPayment);
    }

    // Refund payment
    public PaymentResponse refundPayment(Long id) {
        Payment payment = paymentRepository.findByPayIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Payment not found with id: " + id));

        if (!"COMPLETED".equals(payment.getStatus())) {
            throw new IllegalStateException("Can only refund COMPLETED payments");
        }

        payment.setStatus("REFUNDED");

        Payment updatedPayment = paymentRepository.save(payment);
        return convertToResponse(updatedPayment);
    }

        public String createPaymentURL ( OrderRequest orderRequest) throws Exception {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime createDate = LocalDateTime.now();
            String formattedCreateDate = createDate.format(formatter);
            OrderResponse order  = orderService.createOrder(orderRequest);

            String tmnCode = "2G68WVJ3";
            String secretKey = "VBEI56XQVKA55AV245XA0KRX1Q4DNLFO";
            String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
            String returnUrl = "http://localhost:8080/api/appointment/success/" + order.getOrderId();
            String orderId = UUID.randomUUID().toString().substring(0, 6);

            String currCode = "VND";
            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", tmnCode);
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_CurrCode", currCode);
            vnpParams.put("vnp_TxnRef", orderId);
            vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + order.getOrderId());
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Amount",  order.getTotalAmount() + "00");
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_CreateDate", formattedCreateDate);
            vnpParams.put("vnp_IpAddr", "167.99.74.201");


            StringBuilder signDataBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
                signDataBuilder.append("=");
                signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
                signDataBuilder.append("&");
            }
            signDataBuilder.deleteCharAt(signDataBuilder.length() - 1); // Remove last '&'

            String signData = signDataBuilder.toString();
            String signed = generateHMAC(secretKey, signData);

            vnpParams.put("vnp_SecureHash", signed);

            StringBuilder urlBuilder = new StringBuilder(vnpUrl);
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
                urlBuilder.append("=");
                urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
                urlBuilder.append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove last '&'
            return urlBuilder.toString();
        }

    private String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(keySpec);
        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
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
