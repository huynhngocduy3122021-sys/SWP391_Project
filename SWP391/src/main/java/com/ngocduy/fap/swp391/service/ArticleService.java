package com.ngocduy.fap.swp391.service;


import com.ngocduy.fap.swp391.entity.*;
import com.ngocduy.fap.swp391.enums.ArticleStatus;
import com.ngocduy.fap.swp391.enums.ArticleType;
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
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final BatteryArticleRepository batteryArticleRepository;
    private final CarArticleRepository carArticleRepository;
    private final MotorArticleRepository motorArticleRepository;
    private final ImageRepository imageRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ArticleService(ArticleRepository articleRepository,
                          MemberRepository memberRepository,
                          BatteryArticleRepository batteryArticleRepository,
                          CarArticleRepository carArticleRepository,
                          MotorArticleRepository motorArticleRepository,
                          ImageRepository imageRepository,
                          SubscriptionRepository subscriptionRepository,
                          ModelMapper modelMapper) {
        this.articleRepository = articleRepository;
        this.memberRepository = memberRepository;
        this.batteryArticleRepository = batteryArticleRepository;
        this.carArticleRepository = carArticleRepository;
        this.motorArticleRepository = motorArticleRepository;
        this.imageRepository = imageRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.modelMapper = modelMapper;
    }

    // Helper method to convert Article entity to its base response DTO
    private ArticleResponse convertToArticleResponse(Article article) {
        ArticleResponse response = modelMapper.map(article, ArticleResponse.class);

        if (article.getArticleType() != null) {
            response.setArticleType(article.getArticleType());
        }
        if (article.getStatus() != null) {
            response.setStatus(article.getStatus());
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

    private CarArticleResponse convertToCarArticleResponse(Article article) {
        // Fetch the specific CarArticle data
        CarArticle carArticle = carArticleRepository.findById(article.getArticleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "CarArticle not found for Article id: " + article.getArticleId()));

        // Start with base ArticleResponse (includes all common fields + images)
        CarArticleResponse response = modelMapper.map(convertToArticleResponse(article),
                CarArticleResponse.class);

        // Add specific CarArticle fields
        modelMapper.map(carArticle, response);

        return response;
    }

    private MotorArticleResponse convertToMotorArticleResponse(Article article) {
        // Fetch the specific MotorArticle data
        MotorArticle motorArticle = motorArticleRepository.findById(article.getArticleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "MotorArticle not found for Article id: " + article.getArticleId()));

        // Start with base ArticleResponse (includes all common fields + images)
        MotorArticleResponse response = modelMapper.map(convertToArticleResponse(article),
                MotorArticleResponse.class);

        // Add specific MotorArticle fields
        modelMapper.map(motorArticle, response);

        return response;
    }

    private BatteryArticleResponse convertToBatteryArticleResponse(Article article) {
        // Fetch the specific BatteryArticle data
        BatteryArticle batteryArticle = batteryArticleRepository.findById(article.getArticleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "BatteryArticle not found for Article id: " + article.getArticleId()));

        // Start with base ArticleResponse (includes all common fields + images)
        BatteryArticleResponse response = modelMapper.map(convertToArticleResponse(article),
                BatteryArticleResponse.class);

        // Add specific BatteryArticle fields
        modelMapper.map(batteryArticle, response);

        return response;
    }

    private ArticleResponse convertToSpecificArticleResponse(Article article) {
        ArticleType type = article.getArticleType();

        //safety first
        if(type == null) {
            return convertToArticleResponse(article);
        }

        return switch (type) {
            case CAR_ARTICLE -> convertToCarArticleResponse(article);
            case BATTERY_ARTICLE -> convertToBatteryArticleResponse(article);
            case MOTOR_ARTICLE -> convertToMotorArticleResponse(article);
            default -> convertToArticleResponse(article);
        };

    }

    // Generic helper to save the base Article entity
    private Article saveArticle(ArticleRequest request, ArticleType type) {

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with id: " + request.getMemberId()));

        // Reserve subscription (không trừ slot ngay, chỉ lưu thông tin)
        Subscription reservedSubscription = reservePostingSlot(member.getMemberId());

        // Ensure ModelMapper does not try to map identifier or relations from the request into the entity
        TypeMap<ArticleRequest, Article> articleTypeMap =
                modelMapper.getTypeMap(ArticleRequest.class, Article.class);
        if (articleTypeMap == null) {
            articleTypeMap = modelMapper.createTypeMap(ArticleRequest.class, Article.class);
            articleTypeMap.addMappings(mapper -> {
                mapper.skip(Article::setArticleId);
                mapper.skip(Article::setApprovedBy);
            });
        }

        Article article = articleTypeMap.map(request); // Map common fields without touching ID

        article.setArticleId(null); // Ensure new entity
        article.setArticleType(type); // Set the specific type

        // Relations
        article.setMember(member);
        article.setApprovedBy(null); // Admin is set during approve/reject
        article.setSubscription(reservedSubscription); // Lưu subscription đã reserve (cho relationship)
        // Lưu subscription member_id và package_id để có thể tìm lại sau
        article.setSubscriptionMemberId(reservedSubscription.getId().getMemberId());
        article.setSubscriptionPackageId(reservedSubscription.getId().getPackageId());
        article.setConsumedSlot(false); // Chưa trừ slot, chờ admin duyệt

        // Set status from request or default to PENDING_APPROVAL
        if(request.getStatus() != null) {
            article.setStatus(request.getStatus());
        } else {
            article.setStatus(ArticleStatus.PENDING_APPROVAL);
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

        // Manually map updatable scalar fields from the request instead of using ModelMapper
        // to avoid identifier mapping issues and configuration errors.
        existingArticle.setTitle(request.getTitle());
        existingArticle.setContent(request.getContent());
        existingArticle.setLocation(request.getLocation());
        existingArticle.setContactPhone(request.getContactPhone());
        existingArticle.setPublicDate(request.getPublicDate());

        if (request.getPrice() != null) {
            existingArticle.setPrice(request.getPrice());
        }

        // Set status from request
        if (request.getStatus() != null) {
            try {
                existingArticle.setStatus(request.getStatus());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Article Status: " + request.getStatus());
            }
        }

        return articleRepository.save(existingArticle);
    }

    // Reserve subscription for article (không trừ slot ngay, chỉ reserve)
    private Subscription reservePostingSlot(Long memberId) {
        Subscription subscription = subscriptionRepository
                .findFirstActiveSubscriptionWithRemainingPosts(memberId, LocalDateTime.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Bạn đã hết lượt đăng tin. Vui lòng mua gói đăng tin để tiếp tục."));

        Integer remaining = subscription.getRemainingPosts();
        if (remaining == null || remaining <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bạn đã hết lượt đăng tin. Vui lòng mua gói đăng tin để tiếp tục.");
        }

        // Không trừ slot ngay, chỉ return subscription để lưu vào article
        return subscription;
    }

    // --- General Article Operations ---

    @Transactional(readOnly = true)
    public List<ArticleResponse> getAllArticles() {
        return articleRepository.findAll().stream()
                .filter(article -> !article.isDeleted()) // Only retrieve non-deleted articles
                .map(this::convertToSpecificArticleResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ArticleResponse getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + id));
        if (article.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article with id: " + id + " is deleted.");
        }

        return convertToSpecificArticleResponse(article);
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
        // Manually map fields from request to avoid ModelMapper configuration issues
        existingMotorArticle.setBrand(request.getBrand());
        existingMotorArticle.setYear(request.getYear());
        existingMotorArticle.setVehicleCapacity(request.getVehicleCapacity());
        existingMotorArticle.setOrigin(request.getOrigin());
        existingMotorArticle.setMilesTraveled(request.getMilesTraveled());
        existingMotorArticle.setWarrantyMonths(request.getWarrantyMonths());
        
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
        // Manually map fields from request to avoid ModelMapper configuration issues
        existingBatteryArticle.setVolt(request.getVolt());
        existingBatteryArticle.setCapacity(request.getCapacity());
        existingBatteryArticle.setSize(request.getSize());
        existingBatteryArticle.setWeight(request.getWeight());
        existingBatteryArticle.setBrand(request.getBrand());
        existingBatteryArticle.setOrigin(request.getOrigin());
        existingBatteryArticle.setWarrantyMonths(request.getWarrantyMonths());
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

        // Trừ slot khi admin duyệt (chỉ trừ nếu chưa trừ trước đó)
        if (!Boolean.TRUE.equals(article.getConsumedSlot())) {
            // Tìm subscription từ subscriptionMemberId và subscriptionPackageId đã lưu
            Subscription subscription = null;
            if (article.getSubscriptionMemberId() != null && article.getSubscriptionPackageId() != null) {
                // Tìm subscription từ ID đã lưu
                SubscriptionId subscriptionId = new SubscriptionId(
                        article.getSubscriptionMemberId(),
                        article.getSubscriptionPackageId()
                );
                subscription = subscriptionRepository.findById(subscriptionId).orElse(null);
            }
            
            // Nếu không tìm thấy từ ID đã lưu, tìm lại từ memberId
            if (subscription == null) {
                subscription = subscriptionRepository
                        .findFirstActiveSubscriptionWithRemainingPosts(article.getMember().getMemberId(), LocalDateTime.now())
                        .orElse(null);
                
                // Nếu tìm thấy, lưu ID vào article
                if (subscription != null) {
                    article.setSubscriptionMemberId(subscription.getId().getMemberId());
                    article.setSubscriptionPackageId(subscription.getId().getPackageId());
                }
            }
            
            // Trừ slot nếu có subscription và còn slot
            if (subscription != null) {
                Integer remaining = subscription.getRemainingPosts();
                if (remaining != null && remaining > 0) {
                    subscription.setRemainingPosts(remaining - 1);
                    subscriptionRepository.save(subscription);
                    article.setConsumedSlot(true); // Đánh dấu đã trừ slot
                }
            }
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

        // Hoàn lại slot nếu đã trừ (khi approve rồi reject lại)
        if (Boolean.TRUE.equals(article.getConsumedSlot())) {
            // Tìm subscription từ subscriptionMemberId và subscriptionPackageId đã lưu
            Subscription subscription = null;
            if (article.getSubscriptionMemberId() != null && article.getSubscriptionPackageId() != null) {
                // Tìm subscription từ ID đã lưu
                SubscriptionId subscriptionId = new SubscriptionId(
                        article.getSubscriptionMemberId(),
                        article.getSubscriptionPackageId()
                );
                subscription = subscriptionRepository.findById(subscriptionId).orElse(null);
            }
            
            // Nếu không tìm thấy từ ID đã lưu, tìm lại từ memberId
            if (subscription == null) {
                subscription = subscriptionRepository
                        .findFirstActiveSubscriptionWithRemainingPosts(article.getMember().getMemberId(), LocalDateTime.now())
                        .orElse(null);
            }
            
            // Hoàn lại slot nếu có subscription
            if (subscription != null) {
                Integer remaining = subscription.getRemainingPosts();
                if (remaining != null) {
                    subscription.setRemainingPosts(remaining + 1);
                    subscriptionRepository.save(subscription);
                }
            }
            article.setConsumedSlot(false); // Đánh dấu đã hoàn lại slot
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
                .filter(article -> !article.isDeleted())
                .map(this::convertToArticleResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> getArticlesByStatus(ArticleStatus status) {
        // Changed to findByStatusAndIsDeletedFalse for consistency with soft delete
        // Assumes ArticleRepository has this method: List<Article> findByStatusAndIsDeletedFalse(ArticleStatus status);
        return articleRepository.findByStatus(status).stream()
                .filter(article -> !article.isDeleted())
                .map(this::convertToArticleResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + id));

        article.setDeleted(true);
        articleRepository.save(article);
        return true;

    }
}