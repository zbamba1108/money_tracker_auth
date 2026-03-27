package dev.boog.money_tracker_auth.repositories;

import dev.boog.money_tracker_auth.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    @Query(value = "SELECT r FROM RefreshToken r WHERE r.user.id = :userId")
    Optional<RefreshToken> findByUserId(Long userId);

    Optional<RefreshToken> findByUserIdAndToken(Long userId, String token);
}
