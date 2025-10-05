package com.ngocduy.fap.swp391.service;

import com.ngocduy.fap.swp391.entity.Member;
import com.ngocduy.fap.swp391.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;


@Service
public class TokenService {

    private final String SECRET_KEY = "Y2JUQaJ6bScNaYzejPyyHRXYme4gTHKjY2JUQaJ6bScNaYzejPyyHRXYme4gTHKj";

    @Autowired
    MemberRepository memberRepository;
    public SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    //generate token
    public String generateToken(Member member) {
        return Jwts.builder()
                .subject(member.getMemberId() + "")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSecretKey())
                .compact();
    }


    //verify token
    public Member extractToken(String token) {
       String value = getClaimFromToken(token, Claims::getSubject);
       long id = Long.parseLong(value);
       return memberRepository.findMemberByMemberId(id);
    } // dùng bất cứ khi nào gọi api để xác thực token


    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    } // lấy ra thông tin của token

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

}
