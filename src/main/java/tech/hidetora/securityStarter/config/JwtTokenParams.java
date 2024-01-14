package tech.hidetora.securityStarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hidetora
 * @version 1.0.0
 * @since 2022/04/18
 */
@ConfigurationProperties(prefix = "token")
public record JwtTokenParams (
        long shirtAccessTokenTimeout,
        long longAccessTokenTimeout,
        long refreshTokenTimeout
){}
