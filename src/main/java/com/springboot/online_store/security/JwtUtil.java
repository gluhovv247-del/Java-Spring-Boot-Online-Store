package com.springboot.online_store.security;


import com.springboot.online_store.dtos.user.UserCreateDto;
import com.springboot.online_store.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey key;

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpiration))
                .claim("role", user.getRole())
                .signWith(key)
                .compact();
    }

    public String getUserFromToken(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String token){
        try{
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
