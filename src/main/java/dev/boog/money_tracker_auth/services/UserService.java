package dev.boog.money_tracker_auth.services;

import dev.boog.money_tracker_auth.dto.request.UserRequest;

public interface UserService {

    void save(UserRequest req);

    void delete(String token, UserRequest req);
}
