package com.teampingui.exceptions;

public class NotInDatabaseException extends Exception {

    public NotInDatabaseException() {
    }

    public NotInDatabaseException(String message) {
        super(message);
    }

    public NotInDatabaseException(Throwable cause) {
        super(cause);
    }

    public NotInDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
