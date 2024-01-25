package org.example.user.app.service;

import org.assertj.core.api.Assertions;
import org.example.user.MainAppTestConfig;
import org.example.user.app.domain.model.User;
import org.example.user.app.exception.UserAppAccessDeniedException;
import org.example.user.app.exception.UserAppNotFoundException;
import org.example.user.app.exception.UserAppValidationException;
import org.example.user.app.port.out.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MainAppTestConfig.class})
@DisplayName("UserService should")
class UserServiceImplTest {

    public static final String TEST_USER_LOGIN = "testUser";
    public static final String TEST_PASSWORD = "testPassword";
    public static final String TEST_PASSWORD_NEW = "testPasswordNew";
    public static final String TEST_PASSWORD_NEW_HASHED = "7e39bc00d8f64bc8bae674e2a559dca36bf08f00c080b3e58d162b92cd749fc9";
    public static final String TEST_PASSWORD_HASHED = "1502ddbd9e4262cdae9dde5c3709a64ea4aa38418452474016ff6d97770173cc";
    public static final long TEST_USER_ID = 1L;
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserServiceImpl userService;

    @AfterEach
    public void teardown() {
        Mockito.reset(repository);
    }

    @Test
    @DisplayName("create new user when submitted data is valid")
    void createNewUserWhenSubmittedDataIsValid() {
        when(repository.save(refEq(getNewUserWithoutId()))).thenReturn(getSavedUser());

        User createdUser = userService.create(TEST_USER_LOGIN, TEST_PASSWORD);

        assertThat(createdUser.getId()).isEqualTo(TEST_USER_ID);
        assertThat(createdUser.getLogin()).isEqualTo(TEST_USER_LOGIN);
        assertThat(createdUser.getPassword()).isEqualTo(TEST_PASSWORD_HASHED);
    }

    @DisplayName("throw validation exception when login is not valid")
    @ParameterizedTest
    @MethodSource("provideNotValidLoginsForCreate")
    void createThrowsValidationExceptionWhenLoginIsNotValid(String login) {
        verify(repository, never()).save(any());

        Assertions.assertThatThrownBy(() -> userService.create(login, TEST_PASSWORD))
                .isInstanceOf(UserAppValidationException.class)
                .hasMessageContaining("login");

    }

    @DisplayName("throw validation exception when password is not valid")
    @ParameterizedTest
    @MethodSource("provideNotValidPasswords")
    void create_throwsValidationExceptionWhenPasswordIsNotValid(String password) {
        verify(repository, never()).save(any());

        Assertions.assertThatThrownBy(() -> userService.create(password, TEST_PASSWORD))
                .isInstanceOf(UserAppValidationException.class)
                .hasMessageContaining("login");

    }

    @Test
    @DisplayName("throws exception when login occupied")
    void create_throwsExceptionWhenLoginOccupied() {
        when(repository.existsByLogin(TEST_USER_LOGIN))
                .thenReturn(true);

        Assertions.assertThatThrownBy(() -> userService.create(TEST_USER_LOGIN, TEST_PASSWORD))
                .isInstanceOf(UserAppValidationException.class)
                .hasMessage("login occupied");
    }

    @Test
    @DisplayName("change password when user was recognized")
    void changePassword_whenUserWasRecognized() {
        when(repository.findByLogin(refEq(TEST_USER_LOGIN)))
                .thenReturn(Optional.of(getSavedUser()));

        Assertions.assertThatCode(() -> userService.changePassword(TEST_USER_LOGIN, TEST_PASSWORD, TEST_PASSWORD_NEW))
                .doesNotThrowAnyException();

        verify(repository, times(1))
                .findByLogin(TEST_USER_LOGIN);
        verify(repository, times(1))
                .update(refEq(getUserWithUpdatedPassword()));

    }

    @Test
    @DisplayName("decline request when password was not recognized")
    void changePassword_declineRequestWhenPasswordWasNotRecognized() {
        when(repository.findByLogin(refEq(TEST_USER_LOGIN)))
                .thenReturn(Optional.of(getSavedUser()));

        Assertions.assertThatThrownBy(() -> userService.changePassword(TEST_USER_LOGIN, TEST_PASSWORD_NEW, TEST_PASSWORD))
                .isInstanceOf(UserAppAccessDeniedException.class)
                .hasMessageContaining("old password");

        verify(repository, times(1))
                .findByLogin(TEST_USER_LOGIN);
        verify(repository, times(0))
                .update(refEq(getUserWithUpdatedPassword()));

    }

    @DisplayName("doesn't change password and throws exception when new password is not valid")
    @ParameterizedTest
    @MethodSource("provideNotValidPasswords")
    void changePassword_doesntChangePasswordAndThrowsExceptionWhenNewPasswordIsNotValid(String newPassword) {
        when(repository.findByLogin(refEq(TEST_USER_LOGIN)))
                .thenReturn(Optional.of(getSavedUser()));

        Assertions.assertThatThrownBy(() -> userService.changePassword(TEST_USER_LOGIN, TEST_PASSWORD, newPassword))
                .isInstanceOf(UserAppValidationException.class)
                .hasMessageContaining("newPassword");

        verify(repository, never()).update(any());
    }

    @Test
    @DisplayName("allow logIn when password was recognized")
    void logIn_allowWhenPasswordWasRecognized() {
        when(repository.findByLogin(refEq(TEST_USER_LOGIN)))
                .thenReturn(Optional.of(getSavedUser()));

        Assertions.assertThatCode(() -> userService.logIn(TEST_USER_LOGIN, TEST_PASSWORD))
                .doesNotThrowAnyException();

        verify(repository, times(1))
                .findByLogin(TEST_USER_LOGIN);
    }

    @Test
    @DisplayName("decline logIn when password was not recognized")
    void logIn_declineWhenPasswordWasNotRecognized() {
        when(repository.findByLogin(refEq(TEST_USER_LOGIN)))
                .thenReturn(Optional.of(getSavedUser()));

        Assertions.assertThatThrownBy(() -> userService.logIn(TEST_USER_LOGIN, TEST_PASSWORD_NEW))
                .isInstanceOf(UserAppAccessDeniedException.class)
                .hasMessageContaining("password");

        verify(repository, times(1))
                .findByLogin(TEST_USER_LOGIN);
    }

    @Test
    @DisplayName("get user when login is found")
    void get_userWhenLoginIsFound() {
        when(repository.findByLogin(refEq(TEST_USER_LOGIN)))
                .thenReturn(Optional.of(getSavedUser()));

        Assertions.assertThatCode(() -> userService.get(TEST_USER_LOGIN))
                .doesNotThrowAnyException();

        verify(repository, times(1))
                .findByLogin(TEST_USER_LOGIN);
    }

    @Test
    @DisplayName("decline logIn when user not found")
    void get_declineWhenUserNotFound() {
        when(repository.findByLogin(refEq(TEST_USER_LOGIN)))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.get(TEST_USER_LOGIN))
                .isInstanceOf(UserAppNotFoundException.class)
                .hasMessageContaining("not found");

        verify(repository, times(1))
                .findByLogin(TEST_USER_LOGIN);
    }

    @Test
    @DisplayName("return all users")
    void getAll_returnAllUsers() {
        when(repository.findAll())
                .thenReturn(Stream.of(getSavedUser()));

        Assertions.assertThat(userService.getAll())
                .containsExactlyInAnyOrder(getSavedUser());

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("return empty result if there are no users")
    void getAll_returnEmptyResult() {
        when(repository.findAll())
                .thenReturn(Stream.of());

        Assertions.assertThat(userService.getAll()).isEmpty();

        verify(repository, times(1)).findAll();
    }


    private static Stream<Arguments> provideNotValidLoginsForCreate() {
        return Stream.of(
                Arguments.of((String) null),
                Arguments.of(""),
                Arguments.of(" "),
                Arguments.of("1"),
                Arguments.of("%"),
                Arguments.of("x".repeat(11))
        );
    }

    private static Stream<Arguments> provideNotValidPasswords() {
        return Stream.of(
                Arguments.of((String) null),
                Arguments.of(""),
                Arguments.of("x".repeat(101))
        );
    }

    private static User getNewUserWithoutId() {
        return new User(
                null,
                TEST_USER_LOGIN,
                TEST_PASSWORD_HASHED);
    }

    private static User getSavedUser() {
        return new User(
                TEST_USER_ID,
                TEST_USER_LOGIN,
                TEST_PASSWORD_HASHED);
    }

    private static User getUserWithUpdatedPassword() {
        return new User(
                TEST_USER_ID,
                TEST_USER_LOGIN,
                TEST_PASSWORD_NEW_HASHED);
    }
}