package com.ngocduy.fap.swp391.service;

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
        long totalArticles = articleRepository.count();
        stat.put("totalArticles", totalArticles);


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



    //ti le dang ky cua moi~ goi


    //xu huong dang ky cac goi theo thang


}
