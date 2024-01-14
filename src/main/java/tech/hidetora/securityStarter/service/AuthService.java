package tech.hidetora.securityStarter.service;

import tech.hidetora.securityStarter.dto.request.LoginRequest;
import tech.hidetora.securityStarter.dto.request.RegisterRequest;
import tech.hidetora.securityStarter.dto.UserDTO;
import tech.hidetora.securityStarter.entity.AppUser;

import java.util.Optional;

/**
 * @author hidetora
 * @version 1.0.0
 * @since 2022/04/18
 */
public interface AuthService {
      String login(LoginRequest loginRequest);
      Optional<AppUser> activateUser(String key);
//      UserDTO completePasswordReset(String newPassword, String key);
//      UserDTO requestPasswordReset(String mail);
//      void logout(String token);

      /**
       * Register a user.
       *
       * @param userDTO the entity to save.
       * @return the persisted entity.
       */
      UserDTO registerUser(RegisterRequest userDTO);

      /**
       * Update all information for a specific user, and return the modified user.
       *
       * @param userDTO user to update.
       * @return updated user.
       */
      UserDTO updateUser(UserDTO userDTO);

      /**
       * Delete a user by id.
       * @param id the id of the user.
       * */
      void deleteUser(String id);
}
