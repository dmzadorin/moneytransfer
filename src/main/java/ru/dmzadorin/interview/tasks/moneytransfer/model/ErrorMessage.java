package ru.dmzadorin.interview.tasks.moneytransfer.model;

/**
 * Created by Dmitry Zadorin on 17.02.2018
 */
public class ErrorMessage {
    private final String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
