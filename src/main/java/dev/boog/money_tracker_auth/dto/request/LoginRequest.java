package dev.boog.money_tracker_auth.dto.request;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.*;

@Builder
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank @Length(min = 6, max = 100) String password) {

    @JsonCreator
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
