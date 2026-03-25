package dev.boog.money_tracker_auth.dto.response;

public record LoginResponse(String accessToken, String refreshToken) {
}
