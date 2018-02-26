package ru.dmzadorin.interview.tasks.moneytransfer.model.exceptions;


import ru.dmzadorin.interview.tasks.moneytransfer.model.Currency;

/**
 * Created by Dmitry Zadorin on 26.04.2016.
 */
public class DifferentCurrenciesException extends BaseException {
    public DifferentCurrenciesException(Currency sourceCurrency, Currency recipientCurrency) {
        super("Source currency" + sourceCurrency + " does not match target currency: " + recipientCurrency);
    }
}
