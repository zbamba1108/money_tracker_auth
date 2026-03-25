package dev.boog.money_tracker_auth.services.impl;

import dev.boog.money_tracker_auth.dto.request.LoginRequest;
import dev.boog.money_tracker_auth.dto.response.LoginResponse;
import dev.boog.money_tracker_auth.dto.response.RefreshResponse;
import dev.boog.money_tracker_auth.entities.User;
import dev.boog.money_tracker_auth.repositories.UserRepository;
import dev.boog.money_tracker_auth.services.AuthService;
import dev.boog.money_tracker_auth.services.JwtService;
import dev.boog.money_tracker_auth.utils.Constants;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> user = Optional.of(userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException(Constants.Exceptions.BAD_CREDENTIALS)));

        if (passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            return new LoginResponse(
                    jwtService.generateAccessToken(user.get()),
                    jwtService.generateRefreshToken(user.get())
            );
        }

        throw new BadCredentialsException(Constants.Exceptions.BAD_CREDENTIALS);
    }

    @Override
    public RefreshResponse refreshAccessToken(String refreshToken) {
        Long userId = jwtService.extractUserId(refreshToken);
        Optional<User> user = Optional.of(userRepository.getById(userId)
                .orElseThrow(EntityNotFoundException::new));
        return new RefreshResponse(jwtService.generateAccessToken(user.get()));
    }
}
