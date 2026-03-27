package dev.boog.money_tracker_auth.mappers;

import dev.boog.money_tracker_auth.dto.request.*;
import dev.boog.money_tracker_auth.entities.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

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
