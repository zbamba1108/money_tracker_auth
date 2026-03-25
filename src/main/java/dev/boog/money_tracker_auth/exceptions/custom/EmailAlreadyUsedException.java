package dev.boog.money_tracker_auth.exceptions.custom;

import dev.boog.money_tracker_auth.utils.Constants;
import lombok.Getter;

@Getter
public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(Throwable cause) {
        super(Constants.Exceptions.EMAIL_ALREADY_USED, cause);
    }
}
