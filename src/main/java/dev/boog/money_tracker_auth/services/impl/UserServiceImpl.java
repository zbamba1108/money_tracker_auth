package dev.boog.money_tracker_auth.services.impl;

import dev.boog.money_tracker_auth.dto.request.UserRequest;
import dev.boog.money_tracker_auth.entities.User;
import dev.boog.money_tracker_auth.exceptions.custom.EmailAlreadyUsedException;
import dev.boog.money_tracker_auth.mappers.UserMapper;
import dev.boog.money_tracker_auth.repositories.UserRepository;
import dev.boog.money_tracker_auth.services.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
