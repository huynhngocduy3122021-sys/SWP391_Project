package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.entity.Packages;
import com.ngocduy.fap.swp391.entity.Subscription;
import com.ngocduy.fap.swp391.entity.SubscriptionId;
import com.ngocduy.fap.swp391.exception.exceptions.NotFoundException;
import com.ngocduy.fap.swp391.model.request.SubscriptionRequest;
import com.ngocduy.fap.swp391.model.response.SubscriptionResponse;
import com.ngocduy.fap.swp391.repository.MemberRepository;
import com.ngocduy.fap.swp391.repository.PackagesRepository;
import com.ngocduy.fap.swp391.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PackagesRepository packagesRepository;

    // Get all subscriptions (excluding deleted)
    public List<SubscriptionResponse> getAllSubscriptions() {
        return subscriptionRepository.findByIsDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get subscription by composite ID (excluding deleted)
    public SubscriptionResponse getSubscriptionById(Long memberId, Long packageId) {
        SubscriptionId id = new SubscriptionId(memberId, packageId);
        Subscription subscription = subscriptionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Subscription not found with memberId: " + memberId + " and packageId: " + packageId));
        return convertToResponse(subscription);
    }

    // Get subscriptions by member ID (excluding deleted)
    public List<SubscriptionResponse> getSubscriptionsByMemberId(Long memberId) {
        return subscriptionRepository.findByIdMemberIdAndIsDeletedFalse(memberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get subscriptions by status (excluding deleted)
    public List<SubscriptionResponse> getSubscriptionsByStatus(String status) {
        return subscriptionRepository.findByStatusAndIsDeletedFalse(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Create subscription
    public SubscriptionResponse createSubscription(SubscriptionRequest request) {
        // Check if member exists
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new NotFoundException("Member not found with id: " + request.getMemberId()));

        // Check if package exists
        Packages pkg = packagesRepository.findById(request.getPackageId())
                .orElseThrow(() -> new NotFoundException("Package not found with id: " + request.getPackageId()));

        // Create subscription
        Subscription subscription = new Subscription();
        SubscriptionId id = new SubscriptionId(request.getMemberId(), request.getPackageId());
        subscription.setId(id);
        subscription.setStartDate(request.getStartDate());
        subscription.setEndDate(request.getEndDate());
        subscription.setStatus(request.getStatus());
        subscription.setMember(member);
        subscription.setPkg(pkg);

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return convertToResponse(savedSubscription);
    }

    // Update subscription
    public SubscriptionResponse updateSubscription(Long memberId, Long packageId, SubscriptionRequest request) {
        SubscriptionId id = new SubscriptionId(memberId, packageId);
        Subscription subscription = subscriptionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Subscription not found with memberId: " + memberId + " and packageId: " + packageId));

        subscription.setStartDate(request.getStartDate());
        subscription.setEndDate(request.getEndDate());
        subscription.setStatus(request.getStatus());

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return convertToResponse(updatedSubscription);
    }

    // Delete subscription (soft delete)
    public void deleteSubscription(Long memberId, Long packageId) {
        SubscriptionId id = new SubscriptionId(memberId, packageId);
        Subscription subscription = subscriptionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Subscription not found with memberId: " + memberId + " and packageId: " + packageId));
        subscription.setDeleted(true);
        subscriptionRepository.save(subscription);
    }

    // Convert entity to response
    private SubscriptionResponse convertToResponse(Subscription subscription) {
        SubscriptionResponse response = new SubscriptionResponse();
        response.setMemberId(subscription.getId().getMemberId());
        response.setPackageId(subscription.getId().getPackageId());
        response.setStartDate(subscription.getStartDate());
        response.setEndDate(subscription.getEndDate());
        response.setStatus(subscription.getStatus());

        if (subscription.getMember() != null) {
            response.setMemberName(subscription.getMember().getName());
        }

        if (subscription.getPkg() != null) {
            response.setPackageName(subscription.getPkg().getName());
        }

        return response;
    }
}
