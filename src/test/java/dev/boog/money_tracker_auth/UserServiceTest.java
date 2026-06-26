package dev.boog.money_tracker_auth;

import dev.boog.money_tracker_auth.dto.request.*;
import dev.boog.money_tracker_auth.entities.*;
import dev.boog.money_tracker_auth.exceptions.custom.*;
import dev.boog.money_tracker_auth.mappers.*;
import dev.boog.money_tracker_auth.repositories.*;
import dev.boog.money_tracker_auth.services.*;
import dev.boog.money_tracker_auth.services.impl.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;
import org.springframework.dao.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private static final String ACCESS_TOKEN = "access_token";

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testSave() {
        Mockito.when(userMapper.toEntity(Mockito.any(UserRequest.class))).thenReturn(getUser());

        userService.save(new UserRequest("test@gmail.com", "password"));

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test(expected = EmailAlreadyUsedException.class)
    public void testSaveEmailAlreadyUsed() {
        Mockito.when(userMapper.toEntity(Mockito.any(UserRequest.class))).thenReturn(getUser());
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenThrow(new DataIntegrityViolationException("test"));

        userService.save(new UserRequest("test@gmail.com", "password"));
    }

    @Test
    public void testDelete() {
        Mockito.when(jwtService.validateTypeAndSubstring(Mockito.anyString())).thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.parseSignedClaimsAndExtractUserId(Mockito.anyString())).thenReturn(1L);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getUser()));
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        userService.delete(ACCESS_TOKEN, new UserRequest("test@gmail.com", "password"));

        Mockito.verify(userRepository, Mockito.times(1)).delete(Mockito.any(User.class));
    }

    @Test(expected = InvalidTokenException.class)
    public void testDeleteInvalidToken() {
        Mockito.when(jwtService.validateTypeAndSubstring(Mockito.anyString())).thenThrow(InvalidTokenException.class);

        userService.delete(ACCESS_TOKEN, new UserRequest("test@gmail.com", "password"));
    }

    @Test(expected = InvalidTokenException.class)
    public void testDeleteUserNotFound() {
        Mockito.when(jwtService.validateTypeAndSubstring(Mockito.anyString())).thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.parseSignedClaimsAndExtractUserId(Mockito.anyString())).thenReturn(1L);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(new InvalidTokenException());

        userService.delete(ACCESS_TOKEN, new UserRequest("test@gmail.com", "password"));
    }

    @Test(expected = BadCredentialsException.class)
    public void testDeleteIncorrectPassword() {
        Mockito.when(jwtService.validateTypeAndSubstring(Mockito.anyString())).thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.parseSignedClaimsAndExtractUserId(Mockito.anyString())).thenReturn(1L);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(getUser()));
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        userService.delete(ACCESS_TOKEN, new UserRequest("test@gmail.com", "wrongPassword"));
    }

    private User getUser() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setId(1L);
        user.setPassword("password");

        return user;
    }
}
