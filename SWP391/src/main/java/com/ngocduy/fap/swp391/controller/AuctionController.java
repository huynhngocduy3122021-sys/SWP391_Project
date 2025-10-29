package com.ngocduy.fap.swp391.controller;

import com.ngocduy.fap.swp391.entity.Auction;
import com.ngocduy.fap.swp391.model.request.AuctionRequest;
import com.ngocduy.fap.swp391.model.response.AuctionResponse;
import com.ngocduy.fap.swp391.service.AuctionService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
@SecurityRequirement(name = "api")
public class AuctionController {
    @Autowired
    AuctionService auctionService;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping()
    public ResponseEntity getAuction(){
       List<AuctionResponse> auctions = auctionService.getAllAuction();
       return ResponseEntity.ok(auctions);
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(@Valid @RequestBody AuctionRequest auctionRequest) {
        AuctionResponse auctionResponse = auctionService.createAuction(auctionRequest);

        simpMessagingTemplate.convertAndSend("/topic/auction/create", auctionResponse);

        return ResponseEntity.ok(auctionResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAuction(@PathVariable Long id){
        boolean deleted = auctionService.deleteAuction(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Auction deleted successfully");
    }

    @PutMapping("{id}")
    public ResponseEntity updateAuction(@PathVariable Long id, @Valid @RequestBody AuctionRequest request){
        Auction updated = auctionService.updateAuction(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(updated);
        }

    }


}
