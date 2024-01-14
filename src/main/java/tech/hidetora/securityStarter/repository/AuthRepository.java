package tech.hidetora.securityStarter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.hidetora.securityStarter.entity.Authority;

import java.util.Optional;

/**
 * @author hidetora
 * @version 1.0.0
 * @since 2022/04/18
 */
@Repository
public interface AuthRepository extends JpaRepository<Authority, String> {
/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */

    Optional<Authority> findOneByName(String name);

}
