package dev.boog.money_tracker_auth;

import dev.boog.money_tracker_auth.entities.*;
import dev.boog.money_tracker_auth.exceptions.custom.*;
import dev.boog.money_tracker_auth.services.*;
import dev.boog.money_tracker_auth.services.impl.*;
import dev.boog.money_tracker_auth.utils.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.*;
import io.jsonwebtoken.security.*;
import org.junit.*;


public class JwtServiceTest {

    private JwtService jwtService;

    private JwtParser jwtParser;

    @Before
    public void init() {
        jwtService = new JwtServiceImpl();
        jwtParser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(Constants.Token.SECRET)))
                .build();
    }

    @Test
    public void testGenerateAccessToken() {
        User user = getUser();
        String token = jwtService.generateAccessToken(user);

        Long userId = Long.parseLong(jwtParser.parseSignedClaims(token).getPayload().getSubject());

        Assert.assertEquals(user.getId(), userId);
    }

    @Test
    public void testGenerateAccessTokenNoUserThrowNullPointer() {
        NullPointerException npe = null;
        try {
            jwtService.generateAccessToken(null);
        } catch (NullPointerException e) {
            npe = e;
        }

        Assert.assertNotNull(npe);
    }

    @Test
    public void testGenerateRefreshToken() {
        User user = getUser();
        String token = jwtService.generateRefreshToken(user);

        Long userId = Long.parseLong(jwtParser.parseSignedClaims(token).getPayload().getSubject());

        Assert.assertEquals(user.getId(), userId);
    }

    @Test
    public void testGenerateRefreshTokenNoUserThrowNullPointer() {
        NullPointerException npe = null;
        try {
            jwtService.generateRefreshToken(null);
        } catch (NullPointerException e) {
            npe = e;
        }

        Assert.assertNotNull(npe);
    }

    @Test
    public void testValidateTypeAndSubstring() {
        String token = "Bearer hbgfsgsjdfbglskjfnglksj";
        String expected = token.substring("Bearer ".length());

        String output = jwtService.validateTypeAndSubstring(token);

        Assert.assertEquals(expected, output);
    }

    @Test
    public void testValidateTypeAndSubstringThrowsInvalidTokenException() {
        InvalidTokenException ite = null;

        try {
            jwtService.validateTypeAndSubstring("jdfbsjhgbh");
        } catch (InvalidTokenException e) {
            ite = e;
        }

        Assert.assertNotNull(ite);
    }

    @Test
    public void testParseSignedClaimsAndExtractUserId() {
        User user = getUser();
        String token = jwtService.generateAccessToken(user);

        Long userId = jwtService.parseSignedClaimsAndExtractUserId(token);

        Assert.assertEquals(user.getId(), userId);
    }


    private User getUser() {
        User user = new User();
        user.setId(1L);

        return user;
    }
}
