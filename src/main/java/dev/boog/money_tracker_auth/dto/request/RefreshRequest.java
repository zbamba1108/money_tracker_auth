package dev.boog.money_tracker_auth.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class RefreshRequest {

    @NotBlank
    private final String refreshToken;

    @JsonCreator
    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
