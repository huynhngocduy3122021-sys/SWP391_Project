package com.ngocduy.fap.swp391.repository;

import com.ngocduy.fap.swp391.entity.Auction;
import com.ngocduy.fap.swp391.entity.BID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BIDRepository extends JpaRepository<BID, Long> {


    @Query("SELECT MAX(b.bidAmount) FROM BID b WHERE b.auction = :auction")
    Double findHighestBIDAmount(@Param ("auction") Auction auction);
    BID findBIDByBidId(Long bid);


}
