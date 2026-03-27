package dev.boog.money_tracker_auth.utils;

public class Constants {

    public static class Exceptions {
        public static final String INVALID_TOKEN = "Invalid token";
        public static final String EXPIRED_TOKEN = "Expired token";
        public static final String BAD_CREDENTIALS = "Incorrect email or password";
        public static final String MISSING_BODY = "Missing body";
        public static final String EMAIL_ALREADY_USED = "Email already used";
    }

    public static class Headers {
        public static final String ACCESS_TOKEN = "Access-Token";
        public static final String REFRESH_TOKEN = "Refresh-Token";
    }

    public static class Token {
        public static final String BEARER = "Bearer ";
    }
}
