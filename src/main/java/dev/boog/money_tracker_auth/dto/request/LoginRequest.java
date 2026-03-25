package dev.boog.money_tracker_auth.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@ToString
@Getter
@Builder
public class LoginRequest {

    @Email
    private final String email;

    @NotBlank
    @Length(min = 6, max = 100)
    private final String password;

    @JsonCreator
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
