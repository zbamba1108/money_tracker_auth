package dev.boog.money_tracker_auth.exceptions.custom;

import dev.boog.money_tracker_auth.utils.*;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super(Constants.Exceptions.INVALID_TOKEN);
    }
}
