package com.teampingui.exceptions;

public class HabitDaoException extends Exception {

    public HabitDaoException() {
    }

    public HabitDaoException(String message) {
        super(message);
    }

    public HabitDaoException(Throwable cause) {
        super(cause);
    }

    public HabitDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
