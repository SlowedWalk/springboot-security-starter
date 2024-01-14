package tech.hidetora.securityStarter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tech.hidetora.securityStarter.entity.AppUser;
import tech.hidetora.securityStarter.entity.Authority;
import tech.hidetora.securityStarter.exception.UserNotActivatedException;
import tech.hidetora.securityStarter.repository.UserRepository;

import java.util.List;
import java.util.Locale;

/**
 * @author hidetora
 * @version 1.0.0
 * @since 2022/04/18
 * @description Authenticate a user from the database.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String login) {
        log.debug("loadUserByUsername {}", login);

        if (new EmailValidator().isValid(login, null)) {
            return userRepository
                    .findOneWithAuthoritiesByEmailIgnoreCase(login)
                    .map(user -> createSpringSecurityUser(login, user))
                    .orElseThrow(() -> new UsernameNotFoundException("User with email " + login + " was not found in the database"));
        }

        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        return userRepository
                .findOneWithAuthoritiesByUsername(lowercaseLogin)
                .map(user -> createSpringSecurityUser(lowercaseLogin, user))
                .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
    }

    private User createSpringSecurityUser(String lowercaseLogin, AppUser user) {
        if (!user.isActivated()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        log.debug("creating spring security user");
        List<SimpleGrantedAuthority> grantedAuthorities = user
                .getAuthorities()
                .stream()
                .map(Authority::getName)
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}
