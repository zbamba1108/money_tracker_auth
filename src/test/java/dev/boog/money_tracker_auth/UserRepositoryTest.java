package dev.boog.money_tracker_auth;

import dev.boog.money_tracker_auth.entities.*;
import dev.boog.money_tracker_auth.repositories.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;
import org.springframework.test.context.junit4.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Before
    public void init() {
        DbInitializer.getInstance().initializeUsers(userRepository,1);
    }

    @Test
    public void testFindByEmail() {
        Optional<User> optionalUser = userRepository.findByEmail("user1@gmail.com");
        User user = null;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        }

        Assert.assertNotNull(user);
        Assert.assertEquals("user1@gmail.com", user.getEmail());
    }

    public static User getUser(UserRepository userRepository) {
        Iterable<User> userIterable = userRepository.findAll();
        if (userIterable.iterator().hasNext()) {
            return userIterable.iterator().next();
        }
        return null;
    }
}
