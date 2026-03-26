package dev.boog.money_tracker_auth.repositories;

import dev.boog.money_tracker_auth.entities.RefreshToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    @Query(value = "SELECT r FROM RefreshToken r WHERE r.user.id = :userId")
    Optional<RefreshToken> findByUserId(Long userId);

    Optional<RefreshToken> findByUserIdAndToken(Long userId, String token);
}
