package dev.boog.money_tracker_auth.services;

import dev.boog.money_tracker_auth.entities.User;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoder;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;

@Component
public class JwtServiceImpl implements JwtService {

    private final String secret = "secretlongenoughtobearealsecretwithadditionalcharactershopingnowislongenough"; // TODO replace with ENV_VARIABLE

    private final JwtParser jwtParser = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
            .build();

    @Override
    public String generateAccessToken(User user) {
        Date issuedAt = Date.from(Instant.now());
        Date expiration = Date.from(Instant.now().plus(15L, ChronoUnit.SECONDS));
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
        Date expiration = Date.from(Instant.now().plus(30L, ChronoUnit.SECONDS));
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();
    }

    @Override
    public Long extractUserId(String token) {
        String subject = jwtParser
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return Long.parseLong(subject);
    }
}
