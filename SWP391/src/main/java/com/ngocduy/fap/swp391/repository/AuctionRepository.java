package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Auction findAuctionByAucID(Long id);
}
