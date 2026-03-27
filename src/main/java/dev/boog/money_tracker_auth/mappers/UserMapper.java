package dev.boog.money_tracker_auth.mappers;

import dev.boog.money_tracker_auth.dto.request.UserRequest;
import dev.boog.money_tracker_auth.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public final class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toEntity(UserRequest req) {
        User user = new User();
        String hashPassword = passwordEncoder.encode(req.password());
        user.setEmail(req.email());
        user.setPassword(hashPassword);

        return user;
    }

}
