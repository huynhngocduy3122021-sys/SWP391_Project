package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.enums.ArticleStatus;
import com.ngocduy.fap.swp391.enums.SubscriptionStatus;
import com.ngocduy.fap.swp391.repository.ArticleRepository;
import com.ngocduy.fap.swp391.repository.OrderRepository;
import com.ngocduy.fap.swp391.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    OrderRepository orderRepository;

    public Map<String, Object> getDashBoardStats() {
        Map<String, Object> stat = new HashMap<>();
        //so ng dang ky goi
        long totalSubs = subscriptionRepository.count();
        stat.put("totalSubs", totalSubs);


        //tong so luong article
        long totalArticles = articleRepository.countByStatusAndDeletedFalse(ArticleStatus.APPROVED);
        stat.put("totalArticles", totalArticles);


        //tong doanh thu tu cac don hang da thanh toan
        Double totalRevenuePaid = orderRepository.calculateTotalRevenuePaid();
        stat.put("totalRevenuePaid", totalRevenuePaid);


        return stat;
    }


    public Map<String, Object> getMonthlyRevenue() {
        //doanh thu theo thang
        Map<String, Object> revenueData = new HashMap<>();


        List<Object[]> monthlyRevenue = orderRepository.calculateMonthlyRevenue();
        List<Map<String, Object>> monthlyRevenueList = new ArrayList<>();

        for(Object[] result : monthlyRevenue){
            Map<String, Object> monthlyData = new HashMap<>();
            monthlyData.put("year", result[0]);
            monthlyData.put("month", result[1]);
            monthlyData.put("totalRevenue", result[2]);
            monthlyRevenueList.add(monthlyData);
        }
        revenueData.put("monthlyRevenue", monthlyRevenueList);
        return revenueData;

    }

    public Map<String, Object> getYearlyRevenue() {
        //doanh thu ca nam
        Map<String, Object> revenueData = new HashMap<>();

        List<Object[]> yearlyRevenue = orderRepository.calculateYearlyRevenue();
        List<Map<String, Object>> yearlyRevenueList = new ArrayList<>();

        for(Object[] result : yearlyRevenue){
            Map<String, Object> yearlyData = new HashMap<>();
            yearlyData.put("year", result[0]);
            yearlyData.put("totalRevenue", result[1]);
            yearlyRevenueList.add(yearlyData);
        }
        revenueData.put("yearlyRevenue", yearlyRevenueList);
        return revenueData;
    }

    //phan bo so ng dung` moi~ goi

    public Map<String, Object> getSubscriptionAnalytics(int year) {
        Map<String, Object> result = new HashMap<>();

        List<Object[]> distributionRows = subscriptionRepository.countActiveSubscriptionsByPackage();
        Map<String, Object> distribution = new HashMap<>();
        for (Object[] row : distributionRows) {
            String packageName = (String) row[0];
            Number count = (Number) row[1];
            distribution.put(packageName, count != null ? count.longValue() : 0L);
        }
        result.put("distribution", distribution);

        List<Object[]> monthlyRows = subscriptionRepository.countMonthlyActiveSubscriptionsByPackage(year);
        List<Map<String, Object>> monthlyList = new ArrayList<>();
        for (Object[] row : monthlyRows) {
            Number month = (Number) row[0];
            String packageName = (String) row[1];
            Number count = (Number) row[2];

            Map<String, Object> item = new HashMap<>();
            item.put("month", month != null ? month.intValue() : null);
            item.put("packageName", packageName);
            item.put("count", count != null ? count.longValue() : 0L);
            monthlyList.add(item);
        }
        result.put("monthly", monthlyList);

        return result;
    }

    //ti le dang ky cua moi~ goi


    //xu huong dang ky cac goi theo thang


}
