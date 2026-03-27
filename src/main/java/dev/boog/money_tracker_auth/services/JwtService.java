package dev.boog.money_tracker_auth.services;

import dev.boog.money_tracker_auth.entities.*;

public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String validateTypeAndSubstring(String token);

    Long parseSignedClaimsAndExtractUserId(String token);
}
