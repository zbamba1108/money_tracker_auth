package dev.boog.money_tracker_auth;

import dev.boog.money_tracker_auth.entities.*;
import dev.boog.money_tracker_auth.repositories.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import org.junit.*;
import org.junit.Test;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;
import org.springframework.test.context.junit4.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private Timestamp now;

    @Before
    public void init() {
        DbInitializer.getInstance().initializeUsers(userRepository,1);
        user = getUser(userRepository);
        now = Timestamp.from(Instant.now());
    }

    @Test
    public void testFindByUserIdAndNotExpiredAndNotRevoked() {
        DbInitializer.getInstance().initializeSession(sessionRepository, user, false);
        List<Session> sessionList = sessionRepository.findByUserIdAndNotExpiredAndNotRevoked(user.getId(), now);

        Assert.assertEquals(1, sessionList.size());
        Assert.assertEquals(user.getId(), sessionList.get(0).getUser().getId());
    }

    @Test
    public void testFindByUserIdAndNotExpiredAndNotRevokedUserNotFound() {
        List<Session> sessionList = sessionRepository.findByUserIdAndNotExpiredAndNotRevoked(user.getId(), now);

        Assert.assertTrue(sessionList.isEmpty());
    }

    @Test
    public void testInvalidateActiveSessions() {
        DbInitializer.getInstance().initializeSession(sessionRepository, user, false);

        List<Session> sessionList = sessionRepository.findByUserIdAndNotExpiredAndNotRevoked(user.getId(), now);
        Assert.assertFalse(sessionList.isEmpty());

        sessionRepository.invalidateActiveSessions(user.getId(), Timestamp.from(Instant.now()));
        sessionList = sessionRepository.findByUserIdAndNotExpiredAndNotRevoked(user.getId(), now);

        Assert.assertTrue(sessionList.isEmpty());
    }

    @Test
    public void testInvalidateActiveSessionsNoActiveSessions() {
        DbInitializer.getInstance().initializeSession(sessionRepository, user, true);

        sessionRepository.invalidateActiveSessions(user.getId(), Timestamp.from(Instant.now()));
        List<Session> sessionList = sessionRepository.findByUserIdAndNotExpiredAndNotRevoked(user.getId(), now);

        Assert.assertTrue(sessionList.isEmpty());
    }

    public User getUser(UserRepository userRepository) {
        Iterable<User> userIterable = userRepository.findAll();
        if (userIterable.iterator().hasNext()) {
            return userIterable.iterator().next();
        }
        return null;
    }

}
