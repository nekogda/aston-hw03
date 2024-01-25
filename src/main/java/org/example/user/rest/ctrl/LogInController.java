package org.example.user.rest.ctrl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user.app.port.in.UserService;
import org.example.user.rest.dto.LogInRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@AllArgsConstructor
@Slf4j
public class LogInController {

    private final UserService userService;

    @PostMapping("/login")
    public void logIn(@RequestBody LogInRequest request) {
        log.debug("called with args: request={}", request);
        userService.logIn(
                request.getLogin(),
                request.getPassword());
    }
}