package org.example.user.persistence;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user.app.domain.model.User;
import org.example.user.app.port.out.UserRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
@AllArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate template;

    @Override
    public Stream<User> findAll() {
        log.trace("called");
        return template.queryForStream(
                "SELECT id, login, password FROM USERS",
                new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public User save(User user) {
        log.debug("called with args: user={}", user);
        long id = template.update(
                "INSERT INTO USERS (login, password) VALUES (?, ?)",
                user.getLogin(),
                user.getPassword());
        user.setId(id);
        return user;
    }

    @Override
    public boolean existsByLogin(String login) {
        log.trace("called with args: login={}", login);
        return Optional.ofNullable(template.queryForObject(
                        "SELECT COUNT(1) FROM USERS WHERE login = ? LIMIT 1",
                        Integer.class,
                        login))
                .filter(x -> x > 0)
                .isPresent();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        log.trace("called with args: login={}", login);
        return DataAccessUtils.optionalResult(template.queryForStream(
                "SELECT id, login, password FROM USERS WHERE login = ?",
                new BeanPropertyRowMapper<>(User.class),
                login));
    }

    @Override
    public User update(User user) {
        log.debug("called with args: user={}", user);
        int rowNum = template.update(
                "UPDATE USERS SET login = ?, password = ? WHERE id = ?",
                user.getLogin(), user.getPassword(), user.getId());
        if (rowNum < 1) {
            throw new IllegalStateException("rows not found");
        }
        if (rowNum > 1) {
            throw new IllegalStateException("more than one row affected");
        }
        return user;
    }
}