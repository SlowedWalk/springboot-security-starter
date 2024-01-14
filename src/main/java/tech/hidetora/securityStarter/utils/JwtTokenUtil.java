package tech.hidetora.securityStarter.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;

import static tech.hidetora.securityStarter.utils.Constants.SYSTEM;
import static tech.hidetora.securityStarter.utils.SecurityUtils.AUTHORITIES_KEY;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil implements Serializable {
    @Value("${security.authentication.jwt.base64-secret:myTopSecret}")
    private String secret;

//    @Value("${jwt.issuer}")
//    private String issuer;

    @Value("${security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;
    private final JwtEncoder jwtEncoder;


    //generate token for user
    public String generateToken(UserDetails userDetails, boolean rememberMe) {
        Instant now = Instant.now();
        Instant validity;
        if (rememberMe) {
            validity = now.plus(tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
        } else {
            validity = now.plus(tokenValidityInSeconds, ChronoUnit.SECONDS);
        }

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .issuer(SYSTEM)
                .id(UUID.randomUUID().toString())
                .subject(userDetails.getUsername())
                .claim(AUTHORITIES_KEY, userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" ")))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
