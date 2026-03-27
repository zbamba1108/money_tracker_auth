package dev.boog.money_tracker_auth.services.impl;

import dev.boog.money_tracker_auth.entities.User;
import dev.boog.money_tracker_auth.exceptions.custom.*;
import dev.boog.money_tracker_auth.services.JwtService;
import dev.boog.money_tracker_auth.utils.*;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtServiceImpl implements JwtService {

    public final String secret = "secretlongenoughtobearealsecretwithadditionalcharactershopingnowislongenough"; // TODO replace with ENV_VARIABLE

    private final JwtParser jwtParser = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
            .build();

    @Override
    public String generateAccessToken(User user) {
        Date issuedAt = Date.from(Instant.now());
        Date expiration = Date.from(Instant.now().plus(15L, ChronoUnit.MINUTES));
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
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
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
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
