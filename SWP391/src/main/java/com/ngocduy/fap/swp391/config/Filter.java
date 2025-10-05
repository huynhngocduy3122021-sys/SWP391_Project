package com.ngocduy.fap.swp391.config;

import com.ngocduy.fap.swp391.exception.exceptions.AuthenticationException;
import com.ngocduy.fap.swp391.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    TokenService tokenService;

    private final List<String> PUBLIC_API = List.of(
            "POST:/api/register" ,
            "POST:/api/login"

    );

    public boolean isPublic(String url , String method){
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        if(method.equals("GET")) return true;
        return PUBLIC_API.stream().anyMatch(pattern ->{
            String[] parts = pattern.split(":" , 2);
            if(parts.length != 2)return false;
            String allowedMethod = parts[0];
            String allowedUrl = parts[1];
            return antPathMatcher.match(allowedUrl , url);
        });

    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Filter runnnnn");

        String url = request.getRequestURI();
        String method = request.getMethod();

        if(isPublic(url , method)){
            //api public
            // tất cả access
            filterChain.doFilter(request , response);

        } else {
            //api theo role
            //check role
            // check token
            String token = getToken(request);
            if(token == null) {
                handlerExceptionResolver.resolveException(request , response , null , new AuthenticationException("Empty token!"));
            }

        }


    }
    public String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
