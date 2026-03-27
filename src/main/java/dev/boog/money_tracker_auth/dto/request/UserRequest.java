package dev.boog.money_tracker_auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.*;

@Builder
public record UserRequest(
        @NotBlank @Email String email,
        @NotBlank @Length(min = 6) String password) {

}
