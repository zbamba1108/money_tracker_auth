package dev.boog.money_tracker_auth;

import dev.boog.money_tracker_auth.entities.*;
import dev.boog.money_tracker_auth.repositories.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;
import org.springframework.test.context.junit4.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class RefreshTokenRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private User user;

    @Before
    public void init() {
        DbInitializer.getInstance().initializeUsers(userRepository, 1);
        user = UserRepositoryTest.getUser(userRepository);
        DbInitializer.getInstance().initializeRefreshToken(refreshTokenRepository, user);
    }

    @Test
    public void testFindTokenByUserId() {
        RefreshToken token = refreshTokenRepository.findByUserId(user.getId()).orElse(null);

        Assert.assertNotNull(token);
    }

    @Test
    public void testFindTokenByUserIdNotFound() {
        RefreshToken token = refreshTokenRepository.findByUserId(null).orElse(null);

        Assert.assertNull(token);
    }

    @Test
    public void testFindTokenByUserIdAndToken() {
        RefreshToken token = refreshTokenRepository.findByUserIdAndToken(user.getId(), "refresh_token").orElse(null);

        Assert.assertNotNull(token);
    }

    @Test
    public void testFindTokenByUserIdAndTokenNotFound() {
        RefreshToken token = refreshTokenRepository.findByUserIdAndToken(user.getId(), "refresh_token1").orElse(null);

        Assert.assertNull(token);
    }
}
