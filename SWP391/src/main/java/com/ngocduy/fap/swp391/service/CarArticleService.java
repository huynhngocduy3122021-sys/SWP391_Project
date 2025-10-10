package com.ngocduy.fap.swp391.service;


import com.ngocduy.fap.swp391.entity.Article;
import com.ngocduy.fap.swp391.entity.CarArticle;
import com.ngocduy.fap.swp391.exception.exceptions.ResourceNotFoundException;
import com.ngocduy.fap.swp391.model.request.CarArticleRequest;
import com.ngocduy.fap.swp391.model.response.CarArticleResponse;
import com.ngocduy.fap.swp391.repository.ArticleRepository;
import com.ngocduy.fap.swp391.repository.CarArticleRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ngocduy.fap.swp391.entity.Article.ArticleType;

@Service
public class CarArticleService {


    private final CarArticleRepository carArticleRepository;
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;

    public CarArticleService(CarArticleRepository carArticleRepository, ArticleRepository articleRepository, ModelMapper modelMapper) {
        this.carArticleRepository = carArticleRepository;
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
    }

    public List<CarArticleResponse> getAllActiveCarArticles() { // Return DTO list
        return carArticleRepository.findByDeletedFalse().stream()
                .map(carArticle -> modelMapper.map(carArticle, CarArticleResponse.class))
                .collect(Collectors.toList());
    }

    public Optional<CarArticleResponse> getCarArticleById(Long id) { // Return DTO optional
        return carArticleRepository.findByIdAndDeletedFalse(id)
                .map(carArticle -> modelMapper.map(carArticle, CarArticleResponse.class));
    }

    @Transactional
    public CarArticleResponse createCarArticle(CarArticleRequest carArticleRequest) {
        Article article = articleRepository.findById(carArticleRequest.getArticleId())
                .filter(art -> !art.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Active Article not found with ID: " + carArticleRequest.getArticleId()));

        if (article.getArticleType() != ArticleType.CAR_ARTICLE) {
            throw new IllegalArgumentException("Article must be of type 'CAR_ARTICLE' to create a CarArticle entity for it. Current type: " + article.getArticleType());
        }

        if (carArticleRepository.findByArticle_ArticleId(article.getArticleId()).isPresent()) {
            throw new IllegalArgumentException("An article with ID " + carArticleRequest.getArticleId() + " is already linked to a CarArticle.");
        }

        CarArticle carArticle = modelMapper.map(carArticleRequest, CarArticle.class);
        carArticle.setArticle(article);
        carArticle.setDeleted(false);

        CarArticle savedCarArticle = carArticleRepository.save(carArticle);
        return modelMapper.map(savedCarArticle, CarArticleResponse.class);
    }

    @Transactional
    public CarArticleResponse updateCarArticle(Long id, CarArticleRequest carArticleRequest) {
        CarArticle existingCarArticle = carArticleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active CarArticle not found with ID: " + id));


        modelMapper.map(carArticleRequest, existingCarArticle);

        CarArticle updatedCarArticle = carArticleRepository.save(existingCarArticle);
        return modelMapper.map(updatedCarArticle, CarArticleResponse.class);
    }

    @Transactional
    public void softDeleteCarArticle(Long id) {
        CarArticle carArticle = carArticleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active CarArticle not found with ID: " + id));
        carArticle.setDeleted(true);
        carArticleRepository.save(carArticle);
    }



}
