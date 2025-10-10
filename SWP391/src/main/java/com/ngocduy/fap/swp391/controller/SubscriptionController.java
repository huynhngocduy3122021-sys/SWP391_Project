package com.ngocduy.fap.swp391.controller;

import com.ngocduy.fap.swp391.model.request.SubscriptionRequest;
import com.ngocduy.fap.swp391.model.response.SubscriptionResponse;
import com.ngocduy.fap.swp391.service.SubscriptionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    // Get all subscriptions
    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions() {
        List<SubscriptionResponse> subscriptions = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    // Get subscription by composite ID
    @GetMapping("/getById/{memberId}/{packageId}")
    public ResponseEntity<SubscriptionResponse> getSubscriptionById(
            @PathVariable Long memberId,
            @PathVariable Long packageId) {
        SubscriptionResponse subscription = subscriptionService.getSubscriptionById(memberId, packageId);
        return ResponseEntity.ok(subscription);
    }

    // Get subscriptions by member ID
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByMemberId(@PathVariable Long memberId) {
        List<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptionsByMemberId(memberId);
        return ResponseEntity.ok(subscriptions);
    }

    // Get subscriptions by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByStatus(@PathVariable String status) {
        List<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptionsByStatus(status);
        return ResponseEntity.ok(subscriptions);
    }

    // Create subscription
    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(@RequestBody SubscriptionRequest request) {
        SubscriptionResponse subscription = subscriptionService.createSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    // Update subscription
    @PutMapping("/{memberId}/{packageId}")
    public ResponseEntity<SubscriptionResponse> updateSubscription(
            @PathVariable Long memberId,
            @PathVariable Long packageId,
            @RequestBody SubscriptionRequest request) {
        SubscriptionResponse subscription = subscriptionService.updateSubscription(memberId, packageId, request);
        return ResponseEntity.ok(subscription);
    }

    // Delete subscription
    @DeleteMapping("/{memberId}/{packageId}")
    public ResponseEntity<Void> deleteSubscription(
            @PathVariable Long memberId,
            @PathVariable Long packageId) {
        subscriptionService.deleteSubscription(memberId, packageId);
        return ResponseEntity.noContent().build();
    }
}
