package org.example.user.rest.ctrl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user.app.domain.model.User;
import org.example.user.app.port.in.UserService;
import org.example.user.rest.dto.ChangePasswordRequest;
import org.example.user.rest.dto.CreateUserRequest;
import org.example.user.rest.dto.UserResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createUser(@RequestBody CreateUserRequest request) {
        log.debug("called with args: request={}", request);
        userService.create(request.getLogin(), request.getPassword());
    }

    @PutMapping(value = "/{login}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void changePassword(
            @PathVariable String login,
            @RequestBody ChangePasswordRequest request) {
        log.debug("called with args: login={}, request={}", login, request);
        userService.changePassword(
                login,
                request.getOldPassword(),
                request.getNewPassword());
    }

    @GetMapping("/{login}")
    public UserResponse get(@PathVariable String login) {
        log.debug("called with args: login={}", login);
        User user = userService.get(login);
        return UserResponse.from(user.getLogin());
    }

    @GetMapping
    public List<UserResponse> get() {
        log.debug("called");
        return userService
                .getAll()
                .map(User::getLogin)
                .map(UserResponse::from)
                .toList();
    }
}