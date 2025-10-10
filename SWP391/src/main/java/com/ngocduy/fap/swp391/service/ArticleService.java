package com.ngocduy.fap.swp391.service;


import com.ngocduy.fap.swp391.entity.Admin;
import com.ngocduy.fap.swp391.entity.Article;
import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.model.request.ArticleRequest;
import com.ngocduy.fap.swp391.model.response.ArticleResponse;
import com.ngocduy.fap.swp391.repository.AdminRepository;
import com.ngocduy.fap.swp391.repository.ArticleRepository;
import com.ngocduy.fap.swp391.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static com.ngocduy.fap.swp391.entity.Article.ArticleStatus;
import static com.ngocduy.fap.swp391.entity.Article.ArticleType;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ArticleService(ArticleRepository articleRepository,
                          MemberRepository memberRepository,
                          AdminRepository adminRepository,
                          ModelMapper modelMapper) {
        this.articleRepository = articleRepository;
        this.memberRepository = memberRepository;
        this.adminRepository = adminRepository;
        this.modelMapper = modelMapper;
    }


    private ArticleResponse convertToArticleResponse(Article article) {
        ArticleResponse response = modelMapper.map(article, ArticleResponse.class);

        //convert emu
        if (article.getArticleType() != null) {
            response.setArticleType(article.getArticleType().name());
        } else {
            response.setArticleType(null);
        }
        if (article.getStatus() != null) {
            response.setStatus(article.getStatus().name());
        } else {
            response.setStatus(null);
        }
        // Map Member details
        if (article.getMember() != null) {
            response.setMemberId(article.getMember().getMemberId());
            response.setMemberName(article.getMember().getName());
        } else {
            response.setMemberId(0L);
            response.setMemberName("N/A");
        }

        // Map ApprovedAdmin details
        if (article.getApprovedAdmin() != null) {
            response.setApprovedAdminId(article.getApprovedAdmin().getAdminId());
            response.setApprovedAdminName(article.getApprovedAdmin().getName());
        } else {
            response.setApprovedAdminId(0L);
            response.setApprovedAdminName(null);
        }
        return response;
    }


    @Transactional(readOnly = true)
    public List<ArticleResponse> getAllArticles() {
        return articleRepository.findAll().stream()
                .map(this::convertToArticleResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public ArticleResponse getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + id));
        return convertToArticleResponse(article);
    }


    @Transactional
    public ArticleResponse createArticle(ArticleRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with id: " + request.getMemberId()));

        Article article = modelMapper.map(request, Article.class);

        // Ensure INSERT, not MERGE
        article.setArticleId(null);

        if (request.getArticleType() != null) {
            try {
                article.setArticleType(ArticleType.valueOf(request.getArticleType()));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Article Type: " + request.getArticleType());
            }
        } else {
            // Handle case where articleType is not provided in the request
            // This will result in the @NotNull validation error if not handled
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article type must be provided.");
        }

        // Relations
        article.setMember(member);
        article.setApprovedAdmin(null);

        // Default status if blank
        if (article.getStatus() == null) {
            article.setStatus(ArticleStatus.DRAFT);
        }

        Article savedArticle = articleRepository.save(article);
        return convertToArticleResponse(savedArticle);
    }


    @Transactional
    public ArticleResponse updateArticle(Long id, ArticleRequest request) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + id));

        // Update Member only if provided
        if (request.getMemberId() != 0L &&
                (existingArticle.getMember() == null || !request.getMemberId().equals(existingArticle.getMember().getMemberId()))) {
            Member member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with id: " + request.getMemberId()));
            existingArticle.setMember(member);
        }

        // Handle ApprovedAdmin: null means clear; value means set
        if (request.getApprovedAdminId() == 0L) {
            existingArticle.setApprovedAdmin(null);
        } else {
            Admin approvedAdmin = adminRepository.findById(request.getApprovedAdminId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with id: " + request.getApprovedAdminId()));
            existingArticle.setApprovedAdmin(approvedAdmin);
        }

        // Map scalar fields only (ModelMapper will skip nulls per step 3)
        modelMapper.map(request, existingArticle);

        if (existingArticle.getMember() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member for article cannot be null after update mapping.");
        }

        Article updatedArticle = articleRepository.save(existingArticle);
        return convertToArticleResponse(updatedArticle);
    }


    @Transactional
    public boolean deleteArticle(Long id) {
        return articleRepository.findById(id).map(article ->  {
            article.setDeleted(true);
            articleRepository.save(article);
            return true;
        }).orElse(false);
    }

    @Transactional
    public ArticleResponse approveArticle(Long articleId, Long adminId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + articleId));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with id: " + adminId));

        // Use enum comparison directly
        if (article.getStatus() != ArticleStatus.PENDING_APPROVAL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article is not in 'PENDING_APPROVAL' status and cannot be approved.");
        }

        article.setStatus(ArticleStatus.APPROVED);
        article.setApprovedAdmin(admin);

        Article approvedArticle = articleRepository.save(article);
        return convertToArticleResponse(approvedArticle);
    }

    @Transactional
    public ArticleResponse rejectArticle(Long articleId, Long adminId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found with id: " + articleId));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with id: " + adminId));

        // Use enum comparison directly
        if (article.getStatus() != ArticleStatus.PENDING_APPROVAL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article is not in 'PENDING_APPROVAL' status and cannot be rejected.");
        }

        article.setStatus(ArticleStatus.REJECTED);
        article.setApprovedAdmin(admin);

        Article rejectedArticle = articleRepository.save(article);
        return convertToArticleResponse(rejectedArticle);
    }

    // --- Filtering Operations ---
    @Transactional(readOnly = true)
    public List<ArticleResponse> getArticlesByStatus(ArticleStatus status) { // Method now accepts enum
        return articleRepository.findByStatus(status).stream()
                .map(this::convertToArticleResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> getArticlesByMemberId(Long memberId) {
        return articleRepository.findByMember_MemberId(memberId).stream()
                .map(this::convertToArticleResponse)
                .collect(Collectors.toList());
    }
}