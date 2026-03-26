package dev.boog.money_tracker_auth.services;

import dev.boog.money_tracker_auth.entities.User;

public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    Long validateAndExtractUserId(String token);
}
