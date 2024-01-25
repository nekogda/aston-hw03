package org.example.user.app.port.in;

import org.example.user.app.domain.model.User;

import java.util.stream.Stream;

public interface UserService {

    User create(String login, String password);

    void changePassword(String login, String oldPassword, String newPassword);

    void logIn(String login, String password);

    User get(String login);

    Stream<User> getAll();
}