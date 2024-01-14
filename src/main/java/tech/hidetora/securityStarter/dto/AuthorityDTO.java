package tech.hidetora.securityStarter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import tech.hidetora.securityStarter.entity.Authority;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorityDTO implements Serializable {
    private String name;
    private String description;

    public static Set<AuthorityDTO> toDTO(Set<Authority> authorities) {
        List<AuthorityDTO> collect = authorities.stream().map(authority ->
                AuthorityDTO.builder()
                        .name(authority.getName())
                        .description(authority.getDescription())
                        .build()
        ).collect(Collectors.toList());
        return Set.copyOf(collect);
    }
}
