package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Auction;
import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.model.request.AuctionRequest;
import com.ngocduy.fap.swp391.model.response.AuctionResponse;
import com.ngocduy.fap.swp391.repository.AuctionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionService {

    @Autowired
    TokenService tokenService;
    @Autowired
    AuctionRepository auctionRepository;

    public List<AuctionResponse> getAllAuction(){
        List<Auction> auctions = auctionRepository.findAll();

        return auctions.stream()
                .map(auction -> AuctionResponse.builder()
                        .auctionId(auction.getAucID())
                        .title(auction.getTitle())
                        .description(auction.getDescription())
                        .startTime(auction.getStartTime())
                        .endTime(auction.getEndTime())
                        .price(auction.getPrice())
                        .status(auction.getStatus())
                        .memberName(auction.getMemberId().getName())
                        .build())
                .collect(Collectors.toList());

    }

    //tạo đấu giá

    public AuctionResponse createAuction(AuctionRequest auctionRequest , String token) {
        Member member = tokenService.extractToken(token);

        String status = getAuctionStatus(auctionRequest.getStartTime() , auctionRequest.getEndTime());

        Auction auction = Auction.builder()
                .title(auctionRequest.getTitle())
                .description(auctionRequest.getDescription())
                .startTime(auctionRequest.getStartTime())
                .endTime(auctionRequest.getEndTime())
                .price(auctionRequest.getPrice())
                .increment(auctionRequest.getIncrement())
                .status(status)
                .memberId(member)
                .build();
        auctionRepository.save(auction);
        return AuctionResponse.builder()
                .auctionId(auction.getAucID())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .price(auction.getPrice())
                .status(auction.getStatus())
                .memberName(member.getName())
                .build();

    }

    // hàm private xử lí logic về cập nhật tráng thái của status của đấu giá
      private String getAuctionStatus(LocalDateTime startTime, LocalDateTime endTime) {
         LocalDateTime now = LocalDateTime.now();
         if(now.isBefore(startTime)) {
             return "Not started";
         } if (now.isAfter(endTime)) {
             return "The end";
         } else {
             return "Active";
         }
      }

      public boolean deleteAuction(Long id) {
          return auctionRepository.findById(id).map(auction -> {
              auction.setDeleted(true);
              auctionRepository.save(auction);
              return true;
          }).orElse(false);
      }

      // update
    public Auction updateAuction(Long id, AuctionRequest auctionRequest) {
        Auction existingAuction = auctionRepository.findAuctionByAucID(id);
        if(existingAuction == null) {
            throw new EntityNotFoundException("Auction not found with id: " + id);
        } else {
            existingAuction.setTitle(auctionRequest.getTitle());
            existingAuction.setDescription(auctionRequest.getDescription());
            existingAuction.setStartTime(auctionRequest.getStartTime());
            existingAuction.setEndTime(auctionRequest.getEndTime());
            existingAuction.setPrice(auctionRequest.getPrice());
            existingAuction.setIncrement(auctionRequest.getIncrement());
            existingAuction.setStatus(getAuctionStatus(auctionRequest.getStartTime() , auctionRequest.getEndTime()));
            return auctionRepository.save(existingAuction);
        }
    }



}
