package dev.boog.money_tracker_auth.controllers;

import dev.boog.money_tracker_auth.dto.request.LoginRequest;
import dev.boog.money_tracker_auth.dto.response.LoginResponse;
import dev.boog.money_tracker_auth.dto.response.RefreshResponse;
import dev.boog.money_tracker_auth.services.AuthService;
import dev.boog.money_tracker_auth.utils.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = Constants.Headers.ACCESS_TOKEN) String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestHeader(name = Constants.Headers.REFRESH_TOKEN) String token) {
        return ResponseEntity.ok(authService.refreshAccessToken(token));
    }
}
