package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Auction;
import com.ngocduy.fap.swp391.entity.BID;
import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.exception.exceptions.BIDException;
import com.ngocduy.fap.swp391.model.request.BIDRequest;
import com.ngocduy.fap.swp391.model.response.BIDResponse;
import com.ngocduy.fap.swp391.repository.AuctionRepository;
import com.ngocduy.fap.swp391.repository.BIDRepository;
import com.ngocduy.fap.swp391.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BIDService {

    @Autowired
    TokenService tokenService;
    @Autowired
    BIDRepository bidRepository;
    @Autowired
    AuctionRepository auctionRepository;


    public BIDResponse addBid(BIDRequest bidRequest , String token){


            String cleanToken = token.trim();
            Member member = tokenService.extractToken(cleanToken);
            Auction auction = auctionRepository.findAuctionByAucID(bidRequest.getAuctionId());

            if (auction == null) {
                throw new BIDException("Auction not found" + bidRequest.getAuctionId());
            }

            double minBid = auction.getPrice() + auction.getIncrement();
            if (bidRequest.getBIDAmount() < minBid) {
                throw new BIDException("Bid amount must be greater than or equal to " + minBid);
            }

            Double highestBid = bidRepository.findHighestBIDAmount(auction);
            if (highestBid != null && highestBid >= bidRequest.getBIDAmount()) {
                throw new BIDException("Your bid must be higher than the highest bid: " + highestBid);
            }
            // lưu lại giá của auction
            auction.setPrice(bidRequest.getBIDAmount());
            auctionRepository.save(auction);

            BID bid = BID.builder()
                    .bidAmount(bidRequest.getBIDAmount())
                    .bidDate(LocalDateTime.now())
                    .member(member)
                    .auction(auction)
                    .build();

            bidRepository.save(bid);

            return BIDResponse.builder()
                    .presentPrice(auction.getPrice())
                    .YourPrice(bidRequest.getBIDAmount())
                    .build();
    }


    public List<BID> getAllBids() {
        List<BID> bids = bidRepository.findAll();
        return bids;
    }
}
