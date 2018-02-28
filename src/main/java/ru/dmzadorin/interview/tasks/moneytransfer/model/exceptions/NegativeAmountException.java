package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;

/**
 * Created by Dmitry Zadorin on 28.02.2018
 */
public class NegativeAmountException extends ApplicationException {
    public NegativeAmountException(String message) {
        super(message);
    }
}
