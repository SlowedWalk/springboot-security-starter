package tech.hidetora.securityStarter.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tech.hidetora.securityStarter.entity.AppUser;
import tech.hidetora.securityStarter.entity.Authority;
import tech.hidetora.securityStarter.exception.AuthorityNotFoundException;
import tech.hidetora.securityStarter.repository.AuthRepository;
import tech.hidetora.securityStarter.repository.UserRepository;

import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class ApplicationStartRunner implements CommandLineRunner {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("ApplicationStartRunner.run");

        // Create some authorities
        authRepository.saveAll(Set.of(
                Authority.builder().name("ROLE_USER").description("Simple user authority").build(),
                Authority.builder().name("ROLE_ADMIN").description("Admin user authority").build(),
                Authority.builder().name("ROLE_GUEST").description("Guest user authority").build()
        ));

        // Create an admin user
         userRepository.save(AppUser.builder()
                 .username("admin")
                 .password(passwordEncoder.encode("admin"))
                 .email("admin@admin.com")
                 .activated(true)
                 .authorities(
                         Set.of(authRepository.findOneByName("ROLE_ADMIN")
                                 .orElseThrow(() -> new AuthorityNotFoundException("Admin authority not found"))
                         )
                 )
                 .build());

    }
}
