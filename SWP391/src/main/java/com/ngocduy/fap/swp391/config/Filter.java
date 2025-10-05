package com.ngocduy.fap.swp391.config;

import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.exception.exceptions.AuthenticationException;
import com.ngocduy.fap.swp391.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
    private HandlerExceptionResolver resolver;

    @Autowired
    TokenService tokenService;

    private final List<String> PUBLIC_API = List.of(
            "POST:/api/member",
            "POST:/api/login",
            "GET:/api/member",
            "GET:/swagger-ui/**",
            "GET:/v3/api-docs/**",
            "GET:/swagger-resources/**"

    );

    public boolean isPublicAPI(String uri , String method){
        AntPathMatcher matcher = new AntPathMatcher();

        return PUBLIC_API.stream().anyMatch(pattern -> {
            String[] parts = pattern.split(":", 2);
            if (parts.length != 2) return false;

            String allowedMethod = parts[0];
            String allowedUri = parts[1];

            return matcher.match(allowedUri, uri);
        });

    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        if(isPublicAPI(uri , method)){
            //api public
            // tất cả access
            filterChain.doFilter(request , response);

        } else {
            //api theo role
            //check xem co quyen k
            //=> check token
            String token = getToken(request);
            if(token == null) {
                resolver.resolveException(request , response , null , new AuthenticationException("Empty token!"));
            }
            // co token
            // => verify lai cai token do
            Member member = null;
            try {
                member = tokenService.extractToken(token);
            } catch (ExpiredJwtException expiredJwtException) {
                //1. token het hang
                resolver.resolveException(request , response , null , new AuthenticationException("Expired token!"));
            } catch (MalformedJwtException malformedJwtException) {
                //2. sai token
                resolver.resolveException(request, response, null, new AuthenticationException("Invalid token!"));
            }
            // luu thong tin vua request
            // luu vao session
            UsernamePasswordAuthenticationToken
                    authenToken =
                    new UsernamePasswordAuthenticationToken(member, token, member.getAuthorities());
            authenToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenToken);

            //token chuan
            //dc phep truy cap vao he thong
            filterChain.doFilter(request , response);

        }


    }
    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.substring(7);
    }

}
