package org.example.user.app.port.out;

import org.example.user.app.domain.model.User;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository {
    Stream<User> findAll();

    User save(User user);

    boolean existsByLogin(String login);

    Optional<User> findByLogin(String login);

    User update(User user);
}