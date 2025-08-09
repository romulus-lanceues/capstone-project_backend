package com.mediciationbox.capstone.medication_app.service;

//Handles token creation
//JWT dependencies needed

import com.mediciationbox.capstone.medication_app.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JWTService {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60; //1 hour

    //Load secret key from environment or config
    @Value("${jwt.secret}")
    private String secretString;

    //Method used to sign generated tokens
    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secretString.getBytes());
    }

    public String generateToken(String email){
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    public String getEmailFromToken(String token){
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretString.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //Validate if the token passed in is credible
    public void validateToken(String authHeader){
        String token = authHeader.substring(7);

        try{
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretString.getBytes()))
                    .build()
                    .parseSignedClaims(token);
        }
        //Things to catch ExpiredJwtException, SignatureException,
        // MalformedJwtException, UnsupportedJwtException
        catch (JwtException e){
            throw new InvalidTokenException("Token validation unsuccessful: " + e.getMessage());
        }
        //Catch undefined exceptions
        catch (Exception e){
            throw new InvalidTokenException("An error occurred");
        }
    }

    //Check if the token sent by the frontend is empty
    public void checkIfTokenIsEmpty(String authHeader){

        if(authHeader == null) throw new InvalidTokenException("Your token is empty.");
    }

}
