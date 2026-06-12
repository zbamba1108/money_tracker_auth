package dev.boog.money_tracker_auth;

import dev.boog.money_tracker_auth.entities.*;
import dev.boog.money_tracker_auth.repositories.*;
import java.sql.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;

public class DbInitializer {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static DbInitializer dbInitializer;

    private final Timestamp creationTime = Timestamp.from(Instant.now());
    private final Timestamp expirationTime = Timestamp.from(Instant.now().plus(7, ChronoUnit.DAYS));

    public static DbInitializer getInstance() {
        if (dbInitializer == null) {
            dbInitializer = new DbInitializer();
        }
        return dbInitializer;
    }

    private DbInitializer() {}

    public void initializeUsers(UserRepository userRepository, int insert) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < insert; i++) {
            User user = User.builder()
                    .email("user" + (i+1) + "@gmail.com")
                    .password(passwordEncoder.encode("password"))
                    .build();
            userList.add(user);
        }

        userRepository.saveAll(userList);
    }

    public void initializeSession(SessionRepository sessionRepository, User user, boolean revoked) {
        Session session = new Session();
        session.setCreatedAt(creationTime);
        session.setUser(user);
        session.setExpiration(expirationTime);
        session.setRevoked(revoked);

        sessionRepository.save(session);
    }

    public void initializeRefreshToken(RefreshTokenRepository refreshTokenRepository, User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken("refresh_token");

        refreshTokenRepository.save(refreshToken);
    }
}
