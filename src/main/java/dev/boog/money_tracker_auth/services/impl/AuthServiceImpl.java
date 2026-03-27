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
import dev.boog.money_tracker_auth.utils.HashUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BadCredentialsException(Constants.Exceptions.BAD_CREDENTIALS));

        if (passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
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
            String encodedRefreshToken = HashUtils.sha256(refreshToken);

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
    public void logout(String accessToken) {
        accessToken = jwtService.validateTypeAndSubstring(accessToken);
        Long userId = jwtService.parseSignedClaimsAndExtractUserId(accessToken);
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(InvalidTokenException::new);

        sessionRepository.invalidateActiveSessions(userId, Timestamp.from(Instant.now()));

        refreshTokenRepository.delete(refreshTokenEntity);
    }

    @Transactional
    @Override
    public RefreshResponse refreshAccessToken(String oldRefreshToken) {
        oldRefreshToken = jwtService.validateTypeAndSubstring(oldRefreshToken);
        Long userId = jwtService.parseSignedClaimsAndExtractUserId(oldRefreshToken);
        String encodedOldRefreshToken = HashUtils.sha256(oldRefreshToken);
        RefreshToken refreshTokenEntity = refreshTokenRepository
                .findByUserIdAndToken(userId, encodedOldRefreshToken)
                .orElseThrow(InvalidTokenException::new);

        List<Session> sessionList = sessionRepository
                .findByUserIdAndNotExpiredAndNotRevoked(userId, Timestamp.from(Instant.now()));

        if (sessionList.isEmpty()) {
            throw new InvalidTokenException();
        }

        String newRefreshToken = jwtService.generateRefreshToken(refreshTokenEntity.getUser());
        String encodedNewRefreshToken = HashUtils.sha256(newRefreshToken);
        String newAccessToken = jwtService.generateAccessToken(refreshTokenEntity.getUser());
        refreshTokenEntity.setToken(encodedNewRefreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        return new RefreshResponse(newAccessToken, newRefreshToken);
    }

}
