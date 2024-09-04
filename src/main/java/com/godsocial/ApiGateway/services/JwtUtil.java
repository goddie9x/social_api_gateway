package com.godsocial.ApiGateway.services;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.godsocial.ApiGateway.models.AuthInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.io.Decoders;
import java.util.Date;

@Service
public class JwtUtil {
    private JwtParser jwtParser;

    @Value("${jwt.token.secret}")
    private String secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parser().verifyWith(signingKey).build();
    }

    public AuthInfo extractAuthInfo(String token) {
        if (isTokenExpired(token)) {
            return null;
        }

        Claims claims = getJwsClaimsClaims(token).getPayload();
        String userId = claims.get("userId", String.class);
        String username = claims.get("username", String.class);
        int role = claims.get("role", Integer.class);

        return new AuthInfo(userId, username, role + "");
    }

    public Jws<Claims> getJwsClaimsClaims(String token) {
        Jws<Claims> jwsClaims = jwtParser.parseSignedClaims(token);

        return jwsClaims;
    }

    public boolean isTokenExpired(String token) {
        Claims claims = getJwsClaimsClaims(token).getPayload();
        Date expiration = claims.getExpiration();

        return expiration.before(new Date());
    }

}