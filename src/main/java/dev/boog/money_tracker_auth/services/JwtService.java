package dev.boog.money_tracker_auth.services;

import dev.boog.money_tracker_auth.entities.User;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    Long extractUserId(String token);
}
