package com.ngocduy.fap.swp391.service;


import com.ngocduy.fap.swp391.entity.*;
import com.ngocduy.fap.swp391.model.request.ArticleRequest;
import com.ngocduy.fap.swp391.model.request.BatteryArticleRequest;
import com.ngocduy.fap.swp391.model.request.CarArticleRequest;
import com.ngocduy.fap.swp391.model.request.MotorArticleRequest;
import com.ngocduy.fap.swp391.model.response.ArticleResponse;
import com.ngocduy.fap.swp391.model.response.BatteryArticleResponse;
import com.ngocduy.fap.swp391.model.response.CarArticleResponse;
import com.ngocduy.fap.swp391.model.response.ImageResponse;
import com.ngocduy.fap.swp391.model.response.MotorArticleResponse;
import com.ngocduy.fap.swp391.repository.*;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.ngocduy.fap.swp391.entity.Article.ArticleStatus;
import static com.ngocduy.fap.swp391.entity.Article.ArticleType;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final BatteryArticleRepository batteryArticleRepository;
    private final CarArticleRepository carArticleRepository;
    private final MotorArticleRepository motorArticleRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ArticleService(ArticleRepository articleRepository,
                          MemberRepository memberRepository,
                          BatteryArticleRepository batteryArticleRepository,
                          CarArticleRepository carArticleRepository,
                          MotorArticleRepository motorArticleRepository,
                          ImageRepository imageRepository,
                          ModelMapper modelMapper) {
        this.articleRepository = articleRepository;
        this.memberRepository = memberRepository;
        this.batteryArticleRepository = batteryArticleRepository;
        this.carArticleRepository = carArticleRepository;
        this.motorArticleRepository = motorArticleRepository;
        this.imageRepository = imageRepository;
        this.modelMapper = modelMapper;
    }


    // Helper method to convert Article entity to its base response DTO
    private ArticleResponse convertToArticleResponse(Article article) {
        ArticleResponse response = modelMapper.map(article, ArticleResponse.class);

        if (article.getArticleType() != null) {
            response.setArticleType(article.getArticleType().name());
        }
        if (article.getStatus() != null) {
            response.setStatus(article.getStatus().name());
        }

        if (article.getMember() != null) {
            response.setMemberId(article.getMember().getMemberId());
            response.setMemberName(article.getMember().getName());
        } else {
            response.setMemberId(0L);
            response.setMemberName("N/A");
        }

        if (article.getApprovedBy() != null) {
            response.setApprovedById(article.getApprovedBy().getMemberId());
            response.setApprovedByName(article.getApprovedBy().getName());
        } else {
            response.setApprovedById(0L);
            response.setApprovedByName(null);
        }
        response.setDeleted(article.isDeleted());
        
        // Map images
        if (article.getImages() != null && !article.getImages().isEmpty()) {
            List<ImageResponse> imageResponses = article.getImages().stream()
                    .map(img -> new ImageResponse(img.getImageId(), img.getUrl(), img.isMain()))
                    .collect(Collectors.toList());
            response.setImages(imageResponses);
            
            // Set main image URL for convenience
            article.getImages().stream()
                    .filter(Image::isMain)
                    .findFirst()
                    .ifPresent(img -> response.setMainImageUrl(img.getUrl()));
        }
        
        return response;
    }

    // Generic helper to save the base Article entity
    private Article saveArticle(ArticleRequest request, ArticleType type) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with id: " + request.getMemberId()));

        Article article = modelMapper.map(request, Article.class); // Map common fields
        article.setArticleId(null); // Ensure new entity
        article.setArticleType(type); // Set the specific type

        // Relations
        article.setMember(member);
        article.setApprovedBy(null); // Admin is set during approve/reject

        // Set status from request or default to DRAFT
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                article.setStatus(ArticleStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Article Status: " + request.getStatus());
            }
        } else {
            article.setStatus(ArticleStatus.DRAFT);
        }

        Article savedArticle = articleRepository.save(article);
        
        // Handle images if provided
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            createImagesForArticle(savedArticle, request.getImageUrls());
        }
        
        return savedArticle;
    }
    
    // Helper method to create images for an article
    private void createImagesForArticle(Article article, List<String> imageUrls) {
        for (int i = 0; i < imageUrls.size(); i++) {
            Image image = new Image();
            image.setUrl(imageUrls.get(i));
            image.setArticle(article);
            // Mark first image as main
            image.setMain(i == 0);
            imageRepository.save(image);
        }
    }

    // Generic helper to update the base Article entity
    private Article updateBaseArticle(Long id, ArticleRequest request, ArticleType expectedType) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + id));

        // Ensure the article type in DB matches the type of the update request
        if (existingArticle.getArticleType() != expectedType) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Article with id " + id + " is a " + existingArticle.getArticleType() +
                            ". Cannot update it with a " + expectedType + " request.");
        }

        // Update Member if provided and changed
        if (request.getMemberId() != null && request.getMemberId() != 0L &&
                (existingArticle.getMember() == null || !request.getMemberId().equals(existingArticle.getMember().getMemberId()))) {
            Member member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with id: " + request.getMemberId()));
            existingArticle.setMember(member);
        }

        // Update ApprovedAdmin if provided
        if (request.getApprovedById() != null && request.getApprovedById() != 0L) {
            Member approvedBy = memberRepository.findById(request.getApprovedById())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with id: " + request.getApprovedById()));
            existingArticle.setApprovedBy(approvedBy);
        } else {
            existingArticle.setApprovedBy(null);
        }

        modelMapper.map(request, existingArticle); // Map common fields from the request

        // Set status from request
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                existingArticle.setStatus(ArticleStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Article Status: " + request.getStatus());
            }
        }

        return articleRepository.save(existingArticle);
    }

    // --- General Article Operations ---

    @Transactional(readOnly = true)
    public List<ArticleResponse> getAllArticles() {
        return articleRepository.findAll().stream()
                .filter(article -> !article.isDeleted()) // Only retrieve non-deleted articles
                .map(this::convertToArticleResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ArticleResponse getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + id));
        if (article.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id: " + id + " is deleted.");
        }

        // You might want to return a specific response DTO here based on article.getArticleType()
        // For simplicity, returning the base ArticleResponse which contains common fields.
        // A client would then make another call or handle the specific data if needed.
        return convertToArticleResponse(article);
    }

    // --- Specific Article Creation Methods ---

    @Transactional
    public CarArticleResponse createCarArticle(CarArticleRequest request) {
        Article savedBaseArticle = saveArticle(request, ArticleType.CAR_ARTICLE);

        //modelmapper doesnt seem to work with multi id?
        CarArticle carArticle = new CarArticle();
        carArticle.setBrand(request.getBrand());
        carArticle.setModel(request.getModel());
        carArticle.setYear(request.getYear());
        carArticle.setOrigin(request.getOrigin());
        carArticle.setType(request.getType());
        carArticle.setNumberOfSeat(request.getNumberOfSeat());
        
        // Handle license plate - avoid "string" or empty values
        if (request.getLicensesPlate() != null && 
            !request.getLicensesPlate().isBlank() && 
            !request.getLicensesPlate().equalsIgnoreCase("string")) {
            carArticle.setLicensesPlate(request.getLicensesPlate());
        } else {
            carArticle.setLicensesPlate(null);
        }
        
        carArticle.setRegistrationDeadline(request.getRegistrationDeadline());
        carArticle.setMilesTraveled(request.getMilesTraveled());
        carArticle.setWarrantyPeriodMonths(request.getWarrantyPeriodMonths());

        // Set the Article reference - this will automatically set the ID via @MapsId
        carArticle.setArticle(savedBaseArticle);
        carArticleRepository.save(carArticle);

        // Reload article with images to get the complete data
        Article reloadedArticle = articleRepository.findById(savedBaseArticle.getArticleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reload article"));
        
        // Map to Response DTO using convertToArticleResponse to include images
        CarArticleResponse response = modelMapper.map(convertToArticleResponse(reloadedArticle), CarArticleResponse.class);
        modelMapper.map(carArticle, response);
        return response;
    }

    @Transactional
    public MotorArticleResponse createMotorArticle(MotorArticleRequest request) {
        Article savedBaseArticle = saveArticle(request, ArticleType.MOTOR_ARTICLE);

        MotorArticle motorArticle = new MotorArticle();
        motorArticle.setBrand(request.getBrand());
        motorArticle.setYear(request.getYear());
        motorArticle.setVehicleCapacity(request.getVehicleCapacity());
        
        // Handle license plate - avoid "string" or empty values
        if (request.getLicensesPlate() != null && 
            !request.getLicensesPlate().isBlank() && 
            !request.getLicensesPlate().equalsIgnoreCase("string")) {
            motorArticle.setLicensesPlate(request.getLicensesPlate());
        } else {
            motorArticle.setLicensesPlate(null);
        }
        
        motorArticle.setOrigin(request.getOrigin());
        motorArticle.setMilesTraveled(request.getMilesTraveled());
        motorArticle.setWarrantyMonths(request.getWarrantyMonths());
        
        // Set the Article reference - this will automatically set the ID via @MapsId
        motorArticle.setArticle(savedBaseArticle);
        motorArticleRepository.save(motorArticle);

        // Reload article with images to get the complete data
        Article reloadedArticle = articleRepository.findById(savedBaseArticle.getArticleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reload article"));
        
        // Map to Response DTO using convertToArticleResponse to include images
        MotorArticleResponse response = modelMapper.map(convertToArticleResponse(reloadedArticle), MotorArticleResponse.class);
        modelMapper.map(motorArticle, response);
        return response;
    }

    @Transactional
    public BatteryArticleResponse createBatteryArticle(BatteryArticleRequest request) {
        Article savedBaseArticle = saveArticle(request, ArticleType.BATTERY_ARTICLE);

        BatteryArticle batteryArticle = new BatteryArticle();
        batteryArticle.setVolt(request.getVolt());
        batteryArticle.setCapacity(request.getCapacity());
        batteryArticle.setSize(request.getSize());
        batteryArticle.setWeight(request.getWeight());
        batteryArticle.setBrand(request.getBrand());
        batteryArticle.setOrigin(request.getOrigin());
        batteryArticle.setWarrantyMonths(request.getWarrantyMonths());
        
        // Set the Article reference - this will automatically set the ID via @MapsId
        batteryArticle.setArticle(savedBaseArticle);
        batteryArticleRepository.save(batteryArticle);

        // Reload article with images to get the complete data
        Article reloadedArticle = articleRepository.findById(savedBaseArticle.getArticleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reload article"));
        
        // Map to Response DTO using convertToArticleResponse to include images
        BatteryArticleResponse response = modelMapper.map(convertToArticleResponse(reloadedArticle), BatteryArticleResponse.class);
        modelMapper.map(batteryArticle, response);
        return response;
    }

    // --- Specific Article Update Methods ---

    @Transactional
    public CarArticleResponse updateCarArticle(Long id, CarArticleRequest request) {
        Article updatedBaseArticle = updateBaseArticle(id, request, ArticleType.CAR_ARTICLE);

        CarArticle existingCarArticle = carArticleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CarArticle data not found for Article id: " + id));

        existingCarArticle.setBrand(request.getBrand());
        existingCarArticle.setModel(request.getModel());
        existingCarArticle.setYear(request.getYear());
        existingCarArticle.setOrigin(request.getOrigin());
        existingCarArticle.setType(request.getType());
        existingCarArticle.setNumberOfSeat(request.getNumberOfSeat());
        
        // Handle license plate - avoid "string" or empty values
        if (request.getLicensesPlate() != null && 
            !request.getLicensesPlate().isBlank() && 
            !request.getLicensesPlate().equalsIgnoreCase("string")) {
            existingCarArticle.setLicensesPlate(request.getLicensesPlate());
        } else {
            existingCarArticle.setLicensesPlate(null);
        }
        
        existingCarArticle.setRegistrationDeadline(request.getRegistrationDeadline());
        existingCarArticle.setMilesTraveled(request.getMilesTraveled());
        existingCarArticle.setWarrantyPeriodMonths(request.getWarrantyPeriodMonths());

        existingCarArticle.setArticle(updatedBaseArticle); // Ensure association is correct
        carArticleRepository.save(existingCarArticle);

        // Reload article with images to get the complete data
        Article reloadedArticle = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reload article"));
        
        // Map to Response DTO using convertToArticleResponse to include images
        CarArticleResponse response = modelMapper.map(convertToArticleResponse(reloadedArticle), CarArticleResponse.class);
        modelMapper.map(existingCarArticle, response);
        return response;
    }

    @Transactional
    public MotorArticleResponse updateMotorArticle(Long id, MotorArticleRequest request) {
        Article updatedBaseArticle = updateBaseArticle(id, request, ArticleType.MOTOR_ARTICLE);

        MotorArticle existingMotorArticle = motorArticleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MotorArticle data not found for Article id: " + id));
        modelMapper.map(request, existingMotorArticle);
        
        // Handle license plate - avoid "string" or empty values
        if (request.getLicensesPlate() != null && 
            !request.getLicensesPlate().isBlank() && 
            !request.getLicensesPlate().equalsIgnoreCase("string")) {
            existingMotorArticle.setLicensesPlate(request.getLicensesPlate());
        } else {
            existingMotorArticle.setLicensesPlate(null);
        }
        
        existingMotorArticle.setArticle(updatedBaseArticle);
        motorArticleRepository.save(existingMotorArticle);

        // Reload article with images to get the complete data
        Article reloadedArticle = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reload article"));
        
        // Map to Response DTO using convertToArticleResponse to include images
        MotorArticleResponse response = modelMapper.map(convertToArticleResponse(reloadedArticle), MotorArticleResponse.class);
        modelMapper.map(existingMotorArticle, response);
        return response;
    }

    @Transactional
    public BatteryArticleResponse updateBatteryArticle(Long id, BatteryArticleRequest request) {
        Article updatedBaseArticle = updateBaseArticle(id, request, ArticleType.BATTERY_ARTICLE);

        BatteryArticle existingBatteryArticle = batteryArticleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BatteryArticle data not found for Article id: " + id));
        modelMapper.map(request, existingBatteryArticle);
        existingBatteryArticle.setArticle(updatedBaseArticle);
        batteryArticleRepository.save(existingBatteryArticle);

        // Reload article with images to get the complete data
        Article reloadedArticle = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reload article"));
        
        // Map to Response DTO using convertToArticleResponse to include images
        BatteryArticleResponse response = modelMapper.map(convertToArticleResponse(reloadedArticle), BatteryArticleResponse.class);
        modelMapper.map(existingBatteryArticle, response);
        return response;
    }

    @Transactional
    public ArticleResponse approveArticle(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + articleId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with id: " + memberId));

        if(!"ADMIN".equals(member.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to approve this article");
        }

        article.setStatus(ArticleStatus.APPROVED);
        article.setApprovedBy(member);
        article.setApprovalDate(LocalDateTime.now()); // This now works because approvalDate is in Article entity
        Article updatedArticle = articleRepository.save(article);
        return convertToArticleResponse(updatedArticle);
    }

    @Transactional
    public ArticleResponse rejectArticle(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + articleId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with id: " + memberId));

        if(!"ADMIN".equals(member.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to reject this article");
        }

        article.setStatus(ArticleStatus.REJECTED);
        article.setApprovedBy(member); // member who rejected it
        article.setApprovalDate(LocalDateTime.now()); // Set approval date even on rejection, or add a rejectionDate field
        Article updatedArticle = articleRepository.save(article);
        return convertToArticleResponse(updatedArticle);
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> getArticlesByMemberId(Long memberId) {
        // Changed to findByMember_MemberIdAndIsDeletedFalse for better query generation and consistency
        // Assumes ArticleRepository has this method: List<Article> findByMember_MemberIdAndIsDeletedFalse(Long memberId);
        return articleRepository.findByMember_MemberId(memberId).stream()
                .map(this::convertToArticleResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> getArticlesByStatus(ArticleStatus status) {
        // Changed to findByStatusAndIsDeletedFalse for consistency with soft delete
        // Assumes ArticleRepository has this method: List<Article> findByStatusAndIsDeletedFalse(ArticleStatus status);
        return articleRepository.findByStatus(status).stream()
                .map(this::convertToArticleResponse)
                .collect(Collectors.toList());
    }



    @Transactional
    public boolean deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + id));

        // Soft delete the base article
        article.setDeleted(true);
        articleRepository.save(article);
        return true;

    }
}