package dev.boog.money_tracker_auth.services;

import dev.boog.money_tracker_auth.dto.request.*;
import dev.boog.money_tracker_auth.dto.response.*;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

    void logout(String refreshToken);

    RefreshResponse refreshAccessToken(String oldRefreshToken);
}
