package dev.boog.money_tracker_auth.services.impl;

import dev.boog.money_tracker_auth.dto.request.UserRequest;
import dev.boog.money_tracker_auth.entities.User;
import dev.boog.money_tracker_auth.exceptions.custom.*;
import dev.boog.money_tracker_auth.mappers.UserMapper;
import dev.boog.money_tracker_auth.repositories.UserRepository;
import dev.boog.money_tracker_auth.services.*;
import dev.boog.money_tracker_auth.utils.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           JwtService jwtService,
                           UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(UserRequest req) {
        User user = userMapper.toEntity(req);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailAlreadyUsedException(ex);
        }
    }

    @Override
    public void delete(String token, UserRequest req) {
        token = jwtService.validateTypeAndSubstring(token);
        Long userId = jwtService.parseSignedClaimsAndExtractUserId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(InvalidTokenException::new);

        if (passwordEncoder.matches(req.password(), user.getPassword())
            && req.email().equals(user.getEmail())) {
            userRepository.delete(user);

            return;
        }

        throw new BadCredentialsException(Constants.Exceptions.BAD_CREDENTIALS);
    }

}
