package tech.hidetora.securityStarter.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.hidetora.securityStarter.dto.request.LoginRequest;
import tech.hidetora.securityStarter.dto.request.RegisterRequest;
import tech.hidetora.securityStarter.dto.UserDTO;
import tech.hidetora.securityStarter.dto.response.APIResponse;
import tech.hidetora.securityStarter.entity.AppUser;
import tech.hidetora.securityStarter.exception.UserNotFoundException;
import tech.hidetora.securityStarter.service.AuthService;
import tech.hidetora.securityStarter.service.UserService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static tech.hidetora.securityStarter.utils.Constants.*;

@RestController
@RequestMapping(API_V1_AUTH)
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping(LOGIN)
    public ResponseEntity<APIResponse> login(@RequestBody LoginRequest loginRequest) {
        String login = authService.login(loginRequest);
        return ResponseEntity.ok(APIResponse.builder()
                .status(HttpStatus.OK)
                .message("Login successful")
                        .data(login)
                .statusCode(HttpStatus.OK.value())
                .timestamp(Instant.now().toString())
                .success(true)
                .build());
    }

    @PostMapping(REGISTER)
    public ResponseEntity<APIResponse> register(@RequestBody RegisterRequest registerRequest) {
        UserDTO userDTO = authService.registerUser(registerRequest);

        return ResponseEntity.ok(APIResponse.builder()
                .status(HttpStatus.CREATED)
                .message("Registration successful")
                .data(userDTO)
                .statusCode(HttpStatus.CREATED.value())
                .timestamp(Instant.now().toString())
                .success(true)
                .build());
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping(ACTIVATE)
    public ResponseEntity<APIResponse> activateAccount(@RequestParam(value = "key") String key) {
        Optional<AppUser> user = authService.activateUser(key);
        if (user.isEmpty()) {
            throw new UserNotFoundException("No user was found for this activation key");
        }
        return ResponseEntity.ok(APIResponse.builder()
                .status(HttpStatus.OK)
                .data(user)
                .message("Registration activated")
                .statusCode(HttpStatus.OK.value())
                .timestamp(Instant.now().toString())
                .success(true)
                .build());
    }


    /**
     * {@code GET /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping(AUTHENTICATE)
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * Gets a list of all roles.
     * @return a string list of all roles.
     */
    @GetMapping(AUTHORITIES)
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }

}
