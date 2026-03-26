package dev.boog.money_tracker_auth.utils;

import java.security.MessageDigest;
import java.util.Base64;

public final class HashUtils {

    private static final ThreadLocal<MessageDigest> threadLocal =
            ThreadLocal.withInitial(() -> {
                try {
                    return  MessageDigest.getInstance("SHA-256");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

    private HashUtils() {
        throw new RuntimeException("Utility class");
    }

    public static String sha256(String str) {
        MessageDigest messageDigest = threadLocal.get();
        String hash = Base64.getEncoder().encodeToString(messageDigest.digest(str.getBytes()));
        messageDigest.reset();

        return hash;
    }
}
