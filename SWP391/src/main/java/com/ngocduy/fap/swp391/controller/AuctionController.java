package com.ngocduy.fap.swp391.controller;

import com.ngocduy.fap.swp391.entity.Auction;
import com.ngocduy.fap.swp391.model.request.AuctionRequest;
import com.ngocduy.fap.swp391.model.response.AuctionResponse;
import com.ngocduy.fap.swp391.service.AuctionService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
@SecurityRequirement(name = "api")
public class AuctionController {
    @Autowired
    AuctionService auctionService;

    @GetMapping()
    public ResponseEntity getAuction(Auction auction ){
       List<Auction> auctions = auctionService.getAllAuction(auction);
       return ResponseEntity.ok(auctions);
    }

    @PostMapping
    public ResponseEntity<?> createAuction(
            @RequestBody AuctionRequest auctionRequest,
            @RequestHeader("Authorization") String authHeader) {

        // ✅ Kiểm tra header có giá trị không
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        // ✅ Cắt bỏ prefix "Bearer "
        String token = authHeader.substring(7); // bỏ 7 ký tự đầu "Bearer "

        AuctionResponse response = auctionService.createAuction(auctionRequest, token);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAuction(@PathVariable Long id){
        boolean deleted = auctionService.deleteAuction(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Auction deleted successfully");
    }


}
