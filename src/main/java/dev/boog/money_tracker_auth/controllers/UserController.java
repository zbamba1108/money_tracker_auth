package dev.boog.money_tracker_auth.controllers;

import dev.boog.money_tracker_auth.dto.request.UserRequest;
import dev.boog.money_tracker_auth.services.UserService;
import dev.boog.money_tracker_auth.utils.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/users")
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserRequest request) {
        userService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestHeader(Constants.Headers.ACCESS_TOKEN) String token,
                                           @Valid @RequestBody UserRequest request) {
        userService.delete(token, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
