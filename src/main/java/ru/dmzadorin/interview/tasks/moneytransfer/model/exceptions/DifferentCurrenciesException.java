package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;

/**
 * Created by Dmitry Zadorin on 26.04.2016.
 */
public class DifferentCurrenciesException extends ApplicationException {

    public DifferentCurrenciesException(String message) {
        super(message);
    }
}
