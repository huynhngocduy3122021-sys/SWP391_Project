package com.ngocduy.fap.swp391.controller;

import com.ngocduy.fap.swp391.model.request.BIDRequest;
import com.ngocduy.fap.swp391.model.response.BIDResponse;
import com.ngocduy.fap.swp391.service.BIDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class BIDWebSocketController {
    @Autowired
    BIDService bidService;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    // user gọi tới  /app/bid (qua STOMP)
    @MessageMapping("/bid")
    public void handleBid(@RequestBody BIDRequest bidRequest) {
        try {
            // ✅ Gọi lại hàm addBid để lưu vào database
            BIDResponse bidResponse = bidService.addBid(bidRequest);

            // ✅ Gửi realtime cho tất cả client đang subscribe topic này
            String topic = "/topic/auction/" + bidRequest.getAuctionId();
            simpMessagingTemplate.convertAndSend(topic, bidResponse);

            System.out.println("Bid saved & sent to topic: " + topic);
        } catch (Exception e) {
            System.out.println("PRice : "+bidRequest.getBidAmount());
            System.out.println("Member ID: " + bidRequest.getMember());
            System.out.println("AUC ID: %d" + bidRequest.getAuctionId());
            System.err.println("Lỗi xử lý bid: " + e.getMessage());
        }
    }
}
