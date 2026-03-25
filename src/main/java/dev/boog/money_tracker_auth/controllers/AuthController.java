package dev.boog.money_tracker_auth.controllers;

import dev.boog.money_tracker_auth.dto.request.LoginRequest;
import dev.boog.money_tracker_auth.dto.request.RefreshRequest;
import dev.boog.money_tracker_auth.dto.response.LoginResponse;
import dev.boog.money_tracker_auth.dto.response.RefreshResponse;
import dev.boog.money_tracker_auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshRequest.getRefreshToken()));
    }
}
