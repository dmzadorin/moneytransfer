package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;

/**
 * Created by Dmitry Zadorin on 17.02.2018
 */
public class CurrencyNotSupportedException extends ApplicationException {

    public CurrencyNotSupportedException(String currency) {
        super("Currency " + currency + " is not supported");
    }
}
