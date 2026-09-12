package com.kamran.Docmind.services;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {

    private final SecretKey SECRET_KEY;

    @Value("${jwt.expiration}")
    private   long expiration;

    public JwtService(@Value("${jwt.secret}") String secret){

        this.SECRET_KEY=Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email){
        return Jwts.builder()
                    .subject(email)
                    .issuedAt(new Date())
                    .expiration(
                        new Date(System.currentTimeMillis() + expiration)
                    )
                    .signWith(SECRET_KEY)
                    .compact();
    }

    public String extractEmail(String token){
        return extractAllClaims(token).getSubject();
    }


    public boolean validateToken(String token,UserDetails user){

        final String email=extractEmail(token);

        return (email.equals(user.getUsername()) && isTokenValid(token));
    }


    public boolean isTokenValid(String token){
        try{
            Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token);

            return true;

        }
        catch(Exception e){
            return false;
        }
    }



    private Claims extractAllClaims(String token){
        return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
    }

}
