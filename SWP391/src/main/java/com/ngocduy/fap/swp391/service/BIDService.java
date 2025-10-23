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
    BIDRepository bidRepository;
    @Autowired
    AuctionRepository auctionRepository;
    @Autowired
    MemberRepository memberRepository;


    public BIDResponse addBid(BIDRequest bidRequest ){



            Auction auction = auctionRepository.findAuctionByAucID(bidRequest.getAuctionId());
            Member member = memberRepository.findMemberByMemberId(bidRequest.getMember());

            if (auction == null) {
                throw new BIDException("Auction not found" + bidRequest.getAuctionId());
            }
            if (member == null) {
                throw new BIDException("Member not found" + bidRequest.getMember());
            }

            double minBid = auction.getPrice() + auction.getIncrement();
            if (bidRequest.getBidAmount() < minBid) {
                throw new BIDException("Bid amount must be greater than or equal to " + minBid);
            }

            Double highestBid = bidRepository.findHighestBIDAmount(auction);
            if (highestBid != null && highestBid >= bidRequest.getBidAmount()) {
                throw new BIDException("Your bid must be higher than the highest bid: " + highestBid);
            }
            // lưu lại giá của auction
            auction.setPrice(bidRequest.getBidAmount());
            auctionRepository.save(auction);

            BID bid = BID.builder()
                    .bidAmount(bidRequest.getBidAmount())
                    .bidDate(LocalDateTime.now())
                    .member(member)
                    .auction(auction)
                    .build();

            bidRepository.save(bid);

            return BIDResponse.builder()
                    .presentPrice(auction.getPrice())
                    .YourPrice(bidRequest.getBidAmount())
                    .build();
    }


    public List<BID> getAllBids() {
        List<BID> bids = bidRepository.findAll();
        return bids;
    }

    public BID updateBID(Long BidId, BIDRequest bidRequest){
        BID isExitBid = bidRepository.findBIDByBidId(BidId);
        if (isExitBid == null) {
            throw new BIDException("Bid not found");
        } else {
            isExitBid.setBidAmount(bidRequest.getBidAmount());
            return bidRepository.save(isExitBid);
        }
    }

    public boolean  DeleteBID(Long bidId){
       BID isExitBid = bidRepository.findBIDByBidId(bidId);
       if (isExitBid == null) {
           return false;
       } else {
           bidRepository.deleteById(bidId);
           return true;
       }
    }



}
