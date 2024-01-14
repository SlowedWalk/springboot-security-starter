package tech.hidetora.securityStarter.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.hidetora.securityStarter.dto.UserDTO;
import tech.hidetora.securityStarter.dto.response.APIResponse;
import tech.hidetora.securityStarter.service.UserService;

import java.time.Instant;

import static tech.hidetora.securityStarter.utils.Constants.API_V1_USER;

@RestController
@RequestMapping(API_V1_USER)
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
//    @Cacheable(value = "users", key = "#id")
    public ResponseEntity<APIResponse> getUserById(@PathVariable long id) {
        UserDTO userById = userService.getUserById(id);
        return ResponseEntity.ok(APIResponse.builder()
                .status(HttpStatus.OK)
                .message("User fetched successfully")
                .data(userById)
                .statusCode(HttpStatus.OK.value())
                .timestamp(Instant.now().toString())
                .success(true)
                .build());
    }
}
