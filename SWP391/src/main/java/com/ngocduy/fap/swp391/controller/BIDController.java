package com.ngocduy.fap.swp391.controller;

import com.ngocduy.fap.swp391.entity.BID;
import com.ngocduy.fap.swp391.model.request.BIDRequest;
import com.ngocduy.fap.swp391.model.response.BIDResponse;
import com.ngocduy.fap.swp391.service.BIDService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bid")
@SecurityRequirement(name = "api")
public class BIDController {

    @Autowired
    BIDService bidService;

    @PostMapping
    public ResponseEntity<?> addBId(@Valid @RequestBody BIDRequest bid,
                                    @RequestHeader("Authorization") String authHeader) {
        // Kiểm tra header có giá trị không
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        // Cắt bỏ prefix "Bearer "
        String token = authHeader.substring(7); // bỏ 7 ký tự đầu "Bearer "
        BIDResponse bidResponse = bidService.addBid(bid, token);
        return ResponseEntity.ok(bidResponse);

    }

    @GetMapping

    public ResponseEntity<List<BID>> getAllBids() {
        List<BID> bids = bidService.getAllBids();
        return ResponseEntity.ok(bids); // Trả về status 200 OK và danh sách bids

    }

    @PutMapping("/{id}")
    public ResponseEntity updateBID(@Valid @RequestBody BIDRequest bidRequest, @PathVariable Long id) {
        BID bidUpdate = bidService.updateBID(id , bidRequest);
        if(bidUpdate==null){
            return ResponseEntity.notFound().build();
        } else{
            return ResponseEntity.ok(bidUpdate);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBID(@PathVariable Long id) {
       boolean BidDelete = bidService.DeleteBID(id);
       if(BidDelete){
           return ResponseEntity.ok().build();
       } else {
           return ResponseEntity.notFound().build();
       }
    }



}
