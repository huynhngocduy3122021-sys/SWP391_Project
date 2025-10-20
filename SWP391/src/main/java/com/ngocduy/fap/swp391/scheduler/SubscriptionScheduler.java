package com.ngocduy.fap.swp391.scheduler;

import com.ngocduy.fap.swp391.entity.Subscription;
import com.ngocduy.fap.swp391.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SubscriptionScheduler {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    /**
     * Chạy mỗi ngày lúc 00:00 để kiểm tra và expire các subscription hết hạn
     * Cron format: giây phút giờ ngày tháng thứ
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void checkAndExpireSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        
        // Tìm tất cả subscription ACTIVE đã hết hạn
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findExpiredSubscriptions("ACTIVE", now);
        
        // Cập nhật trạng thái thành EXPIRED
        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus("EXPIRED");
            subscriptionRepository.save(subscription);
        }
        
        if (!expiredSubscriptions.isEmpty()) {
            System.out.println("Expired " + expiredSubscriptions.size() + " subscriptions at " + now);
        }
    }

    /**
     * Chạy mỗi giờ để kiểm tra (optional - nếu muốn check thường xuyên hơn)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void checkExpiredSubscriptionsHourly() {
        LocalDateTime now = LocalDateTime.now();
        
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findExpiredSubscriptions("ACTIVE", now);
        
        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus("EXPIRED");
            subscriptionRepository.save(subscription);
        }
    }
}
