package dev.boog.money_tracker_auth.services;

import dev.boog.money_tracker_auth.dto.request.LoginRequest;
import dev.boog.money_tracker_auth.dto.response.LoginResponse;
import dev.boog.money_tracker_auth.dto.response.RefreshResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

    void logout(String refreshToken);

    RefreshResponse refreshAccessToken(String refreshToken);
}
