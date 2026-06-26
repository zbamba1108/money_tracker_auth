package dev.boog.money_tracker_auth;

import dev.boog.money_tracker_auth.dto.request.*;
import dev.boog.money_tracker_auth.dto.response.*;
import dev.boog.money_tracker_auth.entities.*;
import dev.boog.money_tracker_auth.exceptions.custom.*;
import dev.boog.money_tracker_auth.repositories.*;
import dev.boog.money_tracker_auth.services.*;
import dev.boog.money_tracker_auth.services.impl.*;
import java.sql.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    private static final String ACCESS_TOKEN = "access_token";

    private static final String REFRESH_TOKEN = "refresh_token";

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    public void testLoginSuccessful() {
        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(getUser());
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(jwtService.generateAccessToken(Mockito.any())).thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.generateRefreshToken(Mockito.any())).thenReturn(REFRESH_TOKEN);

        LoginResponse response = authService.login(new LoginRequest("test@gmail.com", "password"));

        Assert.assertEquals(ACCESS_TOKEN, response.accessToken());
        Assert.assertEquals(REFRESH_TOKEN, response.refreshToken());
    }

    @Test(expected = BadCredentialsException.class)
    public void testLoginBadCredentials() {
        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenThrow(BadCredentialsException.class);

        authService.login(new LoginRequest("wrongEmail@gmail.com", "password"));
    }

    @Test(expected = BadCredentialsException.class)
    public void testLoginIncorrectPassword() {
        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(getUser());
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        authService.login(new LoginRequest("test@gmail.com", "wrongPassword"));
    }

    @Test
    public void testLogoutSuccessful() {
        Mockito.when(jwtService.validateTypeAndSubstring(Mockito.anyString())).thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.parseSignedClaimsAndExtractUserId(Mockito.anyString())).thenReturn(1L);
        Mockito.when(refreshTokenRepository.findByUserId(Mockito.anyLong())).thenReturn(getRefreshToken());
        Exception exception = null;

        try {
            authService.logout("Bearer " + ACCESS_TOKEN);
        } catch (Exception e) {
            exception = e;
        }

        Assert.assertNull(exception);
    }

    @Test(expected = InvalidTokenException.class)
    public void testLogoutInvalidAccessToken() {
        Mockito.when(jwtService.validateTypeAndSubstring(Mockito.anyString())).thenThrow(InvalidTokenException.class);

        authService.logout(ACCESS_TOKEN);
    }

    @Test(expected = InvalidTokenException.class)
    public void testLogoutRefreshTokenNotFound() {
        Mockito.when(jwtService.validateTypeAndSubstring(Mockito.anyString())).thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.parseSignedClaimsAndExtractUserId(Mockito.anyString())).thenReturn(1L);
        Mockito.when(refreshTokenRepository.findByUserId(Mockito.anyLong())).thenThrow(InvalidTokenException.class);

        authService.logout(ACCESS_TOKEN);
    }

    @Test
    public void testRefreshAccessToken() {
        Mockito.when(jwtService.validateTypeAndSubstring(Mockito.anyString())).thenReturn(REFRESH_TOKEN);
        Mockito.when(jwtService.parseSignedClaimsAndExtractUserId(Mockito.anyString())).thenReturn(1L);
        Mockito.when(refreshTokenRepository.findByUserIdAndToken(Mockito.anyLong(), Mockito.anyString())).thenReturn(getRefreshToken());
        Mockito.when(sessionRepository.findByUserIdAndNotExpiredAndNotRevoked(Mockito.anyLong(), Mockito.any(Timestamp.class))).thenReturn(getSessions());
        Mockito.when(jwtService.generateAccessToken(Mockito.any(User.class))).thenReturn(ACCESS_TOKEN);
        Mockito.when(jwtService.generateRefreshToken(Mockito.any(User.class))).thenReturn(REFRESH_TOKEN);

        RefreshResponse response = authService.refreshAccessToken(REFRESH_TOKEN);

        Assert.assertEquals(ACCESS_TOKEN, response.accessToken());
        Assert.assertEquals(REFRESH_TOKEN, response.refreshToken());
    }

    @Test(expected = InvalidTokenException.class)
    public void testRefreshAccessTokenInvalidRefreshToken() {
        Mockito.when(jwtService.validateTypeAndSubstring(Mockito.anyString())).thenThrow(InvalidTokenException.class);

        authService.refreshAccessToken(REFRESH_TOKEN);
    }

    @Test(expected = InvalidTokenException.class)
    public void testRefreshAccessTokenRefreshTokenNotFound() {
        Mockito.when(jwtService.validateTypeAndSubstring(Mockito.anyString())).thenReturn(REFRESH_TOKEN);
        Mockito.when(jwtService.parseSignedClaimsAndExtractUserId(Mockito.anyString())).thenReturn(1L);
        Mockito.when(refreshTokenRepository.findByUserIdAndToken(Mockito.anyLong(), Mockito.anyString())).thenThrow(InvalidTokenException.class);

        authService.refreshAccessToken(REFRESH_TOKEN);
    }

    @Test(expected = InvalidTokenException.class)
    public void testRefreshAccessTokenSessionNotFound() {
        Mockito.when(jwtService.validateTypeAndSubstring(Mockito.anyString())).thenReturn(REFRESH_TOKEN);
        Mockito.when(jwtService.parseSignedClaimsAndExtractUserId(Mockito.anyString())).thenReturn(1L);
        Mockito.when(refreshTokenRepository.findByUserIdAndToken(Mockito.anyLong(), Mockito.anyString())).thenReturn(getRefreshToken());
        Mockito.when(sessionRepository.findByUserIdAndNotExpiredAndNotRevoked(Mockito.anyLong(), Mockito.any(Timestamp.class))).thenReturn(new ArrayList<>());

        authService.refreshAccessToken(REFRESH_TOKEN);
    }

    private Optional<User> getUser() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("password");

        return Optional.of(user);
    }

    private Optional<RefreshToken> getRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(REFRESH_TOKEN);
        refreshToken.setId(1L);
        refreshToken.setUser(getUser().isPresent() ? getUser().get() : null);

        return Optional.of(refreshToken);
    }

    private List<Session> getSessions() {
        List<Session> sessions = new ArrayList<>();
        Session session = new Session();
        session.setId(1L);
        sessions.add(session);

        return sessions;
    }

}
