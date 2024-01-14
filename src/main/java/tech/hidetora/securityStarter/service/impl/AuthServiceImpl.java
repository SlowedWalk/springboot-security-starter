package tech.hidetora.securityStarter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hidetora.securityStarter.dto.request.LoginRequest;
import tech.hidetora.securityStarter.dto.request.RegisterRequest;
import tech.hidetora.securityStarter.dto.UserDTO;
import tech.hidetora.securityStarter.entity.AppUser;
import tech.hidetora.securityStarter.entity.Authority;
import tech.hidetora.securityStarter.exception.EmailAlreadyUsedException;
import tech.hidetora.securityStarter.exception.UsernameAlreadyUsedException;
import tech.hidetora.securityStarter.repository.AuthRepository;
import tech.hidetora.securityStarter.repository.UserRepository;
import tech.hidetora.securityStarter.service.AuthService;
import tech.hidetora.securityStarter.service.MailService;
import tech.hidetora.securityStarter.utils.AuthoritiesConstants;
import tech.hidetora.securityStarter.utils.JwtTokenUtil;
import tech.hidetora.securityStarter.utils.RandomUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author hidetora
 * @version 1.0.0
 * @since 2022/04/18
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthRepository authorityRepository;
    private final CacheManager cacheManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;

    @Value("${security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    @Override
    public String login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getLogin(),
                loginRequest.getPassword()
        );
        log.info("AuthenticationToken: {}", authenticationToken);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        User user = (User) authentication.getPrincipal();
        log.info("Authentication {}", authentication.getName());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenUtil.generateToken(user, loginRequest.isRememberMe());
        log.info("JWT {}", token);
        return token;
    }

    /**
     * Activate User using user's activation key
     * @param key The activation key
     */

    @Override
    @Transactional
    public Optional<AppUser> activateUser(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository
                .findOneByActivationKey(key)
                .map(user -> {
                    // activate given user for the registration key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    this.clearUserCaches(user);
//                    userRepository.save(user);
                    log.debug("Activated user: {}", user);
                    return user;
                });
    }

    /**
     * Register a user.
     *
     * @param userDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    @Transactional
    public UserDTO registerUser(RegisterRequest userDTO) {
        userRepository
                .findOneByUsername(userDTO.getUsername().toLowerCase())
                .ifPresent(existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new UsernameAlreadyUsedException();
                    }
                });
        userRepository
                .findOneByEmailIgnoreCase(userDTO.getEmail())
                .ifPresent(existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new EmailAlreadyUsedException();
                    }
                });
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);

        AppUser newUser = AppUser.builder()
                .username(userDTO.getUsername())
//                .login("")
                .email(userDTO.getEmail().toLowerCase())
                .imageUrl(userDTO.getImageUrl())
                // new user gets initially a generated password
                .password(passwordEncoder.encode(userDTO.getPassword()))
                // new user is not active
                .activated(false)
                .activationKey(RandomUtil.generateActivationKey())
                .resetKey(RandomUtil.generateResetKey())
                .authorities(authorities)
                .build();
        userRepository.save(newUser);
        mailService.sendCreationEmail(newUser);
        this.clearUserCaches(newUser);
        log.debug("Created Information for User: {}", newUser);
        return UserDTO.toDTO(newUser);
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    @Override
    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        return null;
    }

    /**
     * Delete a user by id.
     *
     * @param id the id of the user.
     */
    @Override
    @Transactional
    public void deleteUser(String id) {

    }

    private boolean removeNonActivatedUser(AppUser existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        this.clearUserCaches(existingUser);
        return true;
    }

    private void clearUserCaches(AppUser user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getUsername());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired every day, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
                .findAllByActivatedIsFalseAndCreatedAtBefore(Instant.now().minus(3, ChronoUnit.DAYS).toString())
                .forEach(user -> {
                    log.debug("Deleting not activated user {}", user.getUsername());
                    userRepository.delete(user);
                    this.clearUserCaches(user);
                });
    }
}
