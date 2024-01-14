package tech.hidetora.securityStarter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.hidetora.securityStarter.entity.AppUser;

import java.util.List;
import java.util.Optional;

/**
 * @author hidetora
 * @version 1.0.0
 * @since 2022/04/18
 */

/**
 * Spring Data JPA repository for the {@link AppUser} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    String USERS_BY_LOGIN_CACHE = "usersByLogin";
    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    @EntityGraph(attributePaths = "authorities")
//    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<AppUser> findOneWithAuthoritiesByUsername(String login);

//    Optional<AppUser> findOneByLogin(String login);
    Optional<AppUser> findOneByActivationKey(String activationKey);
    Optional<AppUser> findOneByUsername(String login);
    Optional<AppUser> findOneByEmailIgnoreCase(String login);

    @EntityGraph(attributePaths = "authorities")
//    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<AppUser> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    List<AppUser> findAllByActivatedIsFalseAndCreatedAtBefore(String dateTime);
    Page<AppUser> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
