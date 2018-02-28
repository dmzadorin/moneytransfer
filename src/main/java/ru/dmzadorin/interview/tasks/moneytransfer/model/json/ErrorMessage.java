package ru.dmzadorin.interview.tasks.moneytransfer.model.json;

/**
 * Created by Dmitry Zadorin on 17.02.2018
 */
public class ErrorMessage {
    private String message;

    public ErrorMessage() {
    }

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
