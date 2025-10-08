package com.ngocduy.fap.swp391.service;


import com.ngocduy.fap.swp391.entity.Article;
import com.ngocduy.fap.swp391.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    MemberService memberService;

    public Article createArticle(Article article) {
        article.setMemberId(memberService.getCurrentMember());
        return articleRepository.save(article);
    }

}
