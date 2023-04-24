package com.teampingui.exceptions;

public class JournalDaoException extends Exception {

    public JournalDaoException() {
    }

    public JournalDaoException(String message) {
        super(message);
    }

    public JournalDaoException(Throwable cause) {
        super(cause);
    }

    public JournalDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
