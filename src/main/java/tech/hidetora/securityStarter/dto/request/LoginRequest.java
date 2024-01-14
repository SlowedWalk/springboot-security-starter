package tech.hidetora.securityStarter.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NotNull
    @Size(min = 1, max = 50)
    private String login;
    @NotNull
    @Size(min = 4, max = 100)
    private String password;
    private boolean rememberMe;
}
