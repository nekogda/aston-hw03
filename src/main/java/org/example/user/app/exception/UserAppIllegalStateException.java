package org.example.user.app.exception;

public class UserAppIllegalStateException extends UserAppException {
    public UserAppIllegalStateException(String message) {
        super(message);
    }

    public UserAppIllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }
}