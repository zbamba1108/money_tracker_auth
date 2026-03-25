package dev.boog.money_tracker_auth.dto.request;

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
@AllArgsConstructor
public class UserRequest {

    @Email
    private final String email;

    @NotBlank
    @Length(min = 6)
    private final String password;
}
