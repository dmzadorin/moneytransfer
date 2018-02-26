package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;

/**
 * Created by Dmitry Zadorin on 17.02.2018
 */
public class CurrencyNotFoundException extends BaseException{

    public CurrencyNotFoundException(String currency) {
        super("Currency " + currency + " not found");
    }
}
