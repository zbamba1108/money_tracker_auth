package dev.boog.money_tracker_auth.services.impl;

import dev.boog.money_tracker_auth.entities.*;
import dev.boog.money_tracker_auth.exceptions.custom.*;
import dev.boog.money_tracker_auth.services.*;
import dev.boog.money_tracker_auth.utils.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.*;
import io.jsonwebtoken.security.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import org.apache.commons.lang3.*;
import org.springframework.stereotype.*;

@Component
public class JwtServiceImpl implements JwtService {
    
    private final JwtParser jwtParser = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(Constants.Token.SECRET)))
            .build();

    @Override
    public String generateAccessToken(User user) {
        Date issuedAt = Date.from(Instant.now());
        Date expiration = Date.from(Instant.now().plus(15L, ChronoUnit.MINUTES));
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(Constants.Token.SECRET)))
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        Date issuedAt = Date.from(Instant.now());
        Date expiration = Date.from(Instant.now().plus(7L, ChronoUnit.DAYS));
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(Constants.Token.SECRET)))
                .compact();
    }

    @Override
    public String validateTypeAndSubstring(String token) {
        if (StringUtils.isNotBlank(token) && token.startsWith(Constants.Token.BEARER)) {
            return token.substring(Constants.Token.BEARER.length());
        }

        throw new InvalidTokenException();
    }

    @Override
    public Long parseSignedClaimsAndExtractUserId(String token) {
        String subject = jwtParser
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return Long.parseLong(subject);
    }
}
