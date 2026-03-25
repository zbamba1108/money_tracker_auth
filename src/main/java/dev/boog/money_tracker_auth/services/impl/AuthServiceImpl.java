package dev.boog.money_tracker_auth.services.impl;

import dev.boog.money_tracker_auth.dto.request.LoginRequest;
import dev.boog.money_tracker_auth.dto.response.LoginResponse;
import dev.boog.money_tracker_auth.dto.response.RefreshResponse;
import dev.boog.money_tracker_auth.entities.RefreshToken;
import dev.boog.money_tracker_auth.entities.Session;
import dev.boog.money_tracker_auth.entities.User;
import dev.boog.money_tracker_auth.exceptions.custom.InvalidTokenException;
import dev.boog.money_tracker_auth.repositories.RefreshTokenRepository;
import dev.boog.money_tracker_auth.repositories.SessionRepository;
import dev.boog.money_tracker_auth.repositories.UserRepository;
import dev.boog.money_tracker_auth.services.AuthService;
import dev.boog.money_tracker_auth.services.JwtService;
import dev.boog.money_tracker_auth.utils.Constants;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final SessionRepository sessionRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           SessionRepository sessionRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException(Constants.Exceptions.BAD_CREDENTIALS));

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            Instant now = Instant.now();
            sessionRepository.invalidateActiveSessions(user.getId(), Timestamp.from(now));

            Session session = Session.builder()
                    .user(user)
                    .createdAt(Timestamp.from(now))
                    .expiration(Timestamp.from(now.plus(30L, ChronoUnit.DAYS)))
                    .build();

            sessionRepository.save(session);

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            String encodedRefreshToken = passwordEncoder.encode(refreshToken);

            RefreshToken refreshTokenEntity = refreshTokenRepository
                    .findByUserId(user.getId())
                    .map(r -> {
                        r.setToken(encodedRefreshToken);
                        return r;
                    })
                    .orElseGet(() -> RefreshToken.builder()
                            .token(encodedRefreshToken)
                            .user(user)
                            .build());

            refreshTokenRepository.save(refreshTokenEntity);

            return new LoginResponse(accessToken, refreshToken);
        }

        throw new BadCredentialsException(Constants.Exceptions.BAD_CREDENTIALS);
    }

    @Transactional
    @Override
    public void logout(String refreshToken) {
        Long userId = jwtService.extractUserId(refreshToken);
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(InvalidTokenException::new);

        if (!passwordEncoder.matches(refreshToken, refreshTokenEntity.getToken())) {
            throw new InvalidTokenException();
        }

        sessionRepository.invalidateActiveSessions(userId, Timestamp.from(Instant.now()));

        refreshTokenRepository.delete(refreshTokenEntity);
    }

    @Transactional
    @Override
    public RefreshResponse refreshAccessToken(String refreshToken) {
        Long userId = jwtService.extractUserId(refreshToken);
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(InvalidTokenException::new);

        if (!passwordEncoder.matches(refreshToken, refreshTokenEntity.getToken())
                || sessionRepository.findByUserIdAndNotExpiredAndNotRevoked(userId, Timestamp.from(Instant.now())).isEmpty()) {
            throw new InvalidTokenException();
        }

        String refreshTokenString = jwtService.generateRefreshToken(refreshTokenEntity.getUser());
        String accessTokenString = jwtService.generateAccessToken(refreshTokenEntity.getUser());
        refreshTokenEntity.setToken(passwordEncoder.encode(refreshTokenString));
        refreshTokenRepository.save(refreshTokenEntity);

        return new RefreshResponse(accessTokenString, refreshTokenString);
    }

}
