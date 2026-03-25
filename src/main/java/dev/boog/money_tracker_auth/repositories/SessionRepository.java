package dev.boog.money_tracker_auth.repositories;

import dev.boog.money_tracker_auth.entities.Session;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface SessionRepository extends CrudRepository<Session, Long> {

    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.expiration > :expiration AND NOT s.revoked")
    List<Session> findByUserIdAndNotExpiredAndNotRevoked(Long userId, Timestamp expiration);

    @Transactional
    @Modifying
    @Query("UPDATE Session s SET s.revoked = true WHERE s.user.id = :userId AND s.expiration > :expiration AND NOT s.revoked")
    void invalidateActiveSessions(Long userId, Timestamp expiration);
}
