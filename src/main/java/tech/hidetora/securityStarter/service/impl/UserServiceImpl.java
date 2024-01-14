package tech.hidetora.securityStarter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hidetora.securityStarter.dto.UserDTO;
import tech.hidetora.securityStarter.entity.AppUser;
import tech.hidetora.securityStarter.entity.Authority;
import tech.hidetora.securityStarter.exception.UserNotFoundException;
import tech.hidetora.securityStarter.repository.AuthRepository;
import tech.hidetora.securityStarter.repository.UserRepository;
import tech.hidetora.securityStarter.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(long id) {
        log.info("Fetching user with id {}", id);
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user with id " + id + " not found"));
        return UserDTO.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        List<AppUser> users = userRepository.findAll();
        return users.stream().map(UserDTO::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::toDTO);
    }

    @Override
    public void deleteUser(String username) {
        userRepository
                .findOneByUsername(username)
                .ifPresent(user -> {
                    userRepository.delete(user);
                    log.debug("Deleted User: {}", user);
                });
    }

    @Override
    public List<String> getAuthorities() {
        return authRepository.findAll().stream().map(Authority::getName).toList();
    }
}
