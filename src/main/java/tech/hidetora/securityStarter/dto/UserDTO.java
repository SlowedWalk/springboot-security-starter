package tech.hidetora.securityStarter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import tech.hidetora.securityStarter.entity.AppUser;
import tech.hidetora.securityStarter.utils.Constants;


import java.util.Set;

/**
 * @author hidetora
 * @version 1.0.0
 * @since 2022/04/18
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private String username;
    @Pattern(regexp = Constants.LOGIN_REGEX)
    private String login;
    private String email;
    private String imageUrl;

    private Set<AuthorityDTO> authorities;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String activationKey;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String updatedAt;

    public static UserDTO toDTO(AppUser newUser) {
        return UserDTO.builder()
                .username(newUser.getUsername())
                .email(newUser.getEmail())
                .authorities(AuthorityDTO.toDTO(newUser.getAuthorities()))
                .imageUrl(newUser.getImageUrl())
                .activationKey(newUser.getActivationKey())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();
    }

    public static AppUser toEntity(UserDTO userDTO) {
        return AppUser.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .imageUrl(userDTO.getImageUrl())
                .build();
    }
}
