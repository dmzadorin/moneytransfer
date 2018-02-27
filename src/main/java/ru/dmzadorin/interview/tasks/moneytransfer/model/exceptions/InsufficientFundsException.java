package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;

/**
 * Created by Dmitry Zadorin on 26.04.2016.
 */
public class InsufficientFundsException extends ApplicationException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
