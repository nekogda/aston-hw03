package org.example.user.rest.dto;

import lombok.Value;

import java.util.List;

@Value(staticConstructor = "from")
public class ErrorResponse {
    List<String> messages;
}