package org.example.user.rest.dto;

import lombok.Value;

@Value(staticConstructor = "from")
public class UserResponse {
    String login;
}